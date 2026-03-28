package com.medfund.rules.compiler;

import com.medfund.rules.model.Condition;
import com.medfund.rules.model.ConditionGroup;
import com.medfund.rules.model.Operator;
import com.medfund.rules.model.RuleDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Compiles JSON-based {@link RuleDefinition} objects into Drools DRL strings.
 * <p>
 * Conditions are grouped by fact type so that all constraints on the same fact
 * appear within a single Drools pattern (e.g. one {@code $claim : ClaimFact(...)} block).
 */
@Component
public class DrlCompiler {

    private static final String IMPORTS =
            "import com.medfund.rules.fact.ClaimFact;\n" +
            "import com.medfund.rules.fact.ClaimDetailFact;\n" +
            "import com.medfund.rules.fact.MemberFact;\n" +
            "import com.medfund.rules.fact.DependantFact;\n" +
            "import com.medfund.rules.fact.ProviderFact;\n" +
            "import com.medfund.rules.fact.FamilyFact;\n" +
            "import java.math.BigDecimal;\n";

    private static final Map<String, FactMapping> FACT_MAPPINGS = Map.of(
            "claim", new FactMapping("$claim", "ClaimFact"),
            "member", new FactMapping("$member", "MemberFact"),
            "dependant", new FactMapping("$dependant", "DependantFact"),
            "provider", new FactMapping("$provider", "ProviderFact"),
            "claimDetail", new FactMapping("$detail", "ClaimDetailFact"),
            "family", new FactMapping("$family", "FamilyFact")
    );

    /**
     * Compile a single rule definition into a complete DRL string (with imports).
     */
    public String compile(RuleDefinition rule) {
        StringBuilder drl = new StringBuilder();
        drl.append(IMPORTS).append("\n");
        appendRule(drl, rule);
        return drl.toString();
    }

    /**
     * Compile multiple rule definitions into a single DRL string with shared imports.
     */
    public String compileAll(List<RuleDefinition> rules) {
        StringBuilder drl = new StringBuilder();
        drl.append(IMPORTS).append("\n");
        for (RuleDefinition rule : rules) {
            if (rule.isEnabled()) {
                appendRule(drl, rule);
                drl.append("\n");
            }
        }
        return drl.toString();
    }

    private void appendRule(StringBuilder drl, RuleDefinition rule) {
        drl.append("rule \"").append(escapeQuotes(rule.getName())).append("\"\n");
        drl.append("  salience ").append(rule.getPriority()).append("\n");
        drl.append("  when\n");
        appendConditions(drl, rule.getConditions());
        drl.append("  then\n");
        appendAction(drl, rule);
        drl.append("end\n");
    }

    /**
     * Groups conditions by fact type and generates one Drools pattern per fact.
     * Within each pattern, conditions are joined by the group's logical operator.
     */
    private void appendConditions(StringBuilder drl, ConditionGroup group) {
        if (group == null || group.getItems() == null || group.getItems().isEmpty()) {
            // No conditions — always matches. Insert a ClaimFact pattern so $claim is bound.
            drl.append("    $claim : ClaimFact()\n");
            return;
        }

        // Group conditions by fact type prefix, preserving insertion order
        Map<String, List<Condition>> byFact = new LinkedHashMap<>();
        for (Condition condition : group.getItems()) {
            String factType = getFactType(condition.getField());
            byFact.computeIfAbsent(factType, k -> new ArrayList<>()).add(condition);
        }

        // Ensure $claim is always bound (needed for actions)
        if (!byFact.containsKey("claim")) {
            drl.append("    $claim : ClaimFact()\n");
        }

        boolean isOr = "OR".equalsIgnoreCase(group.getOperator());

        for (Map.Entry<String, List<Condition>> entry : byFact.entrySet()) {
            String factType = entry.getKey();
            List<Condition> conditions = entry.getValue();
            FactMapping mapping = FACT_MAPPINGS.get(factType);
            if (mapping == null) {
                continue;
            }

            String joiner = isOr ? " || " : ", ";
            String constraints = conditions.stream()
                    .map(this::toConstraint)
                    .collect(Collectors.joining(joiner));

            drl.append("    ").append(mapping.variable).append(" : ")
               .append(mapping.className).append("(").append(constraints).append(")\n");
        }
    }

    /**
     * Converts a single Condition into a Drools constraint expression.
     */
    private String toConstraint(Condition condition) {
        String property = getProperty(condition.getField());
        String op = mapOperator(condition.getOperator());
        String value = formatValue(condition.getValue());
        return property + " " + op + " " + value;
    }

    /**
     * Maps the action type to DRL consequence code.
     */
    private void appendAction(StringBuilder drl, RuleDefinition rule) {
        if (rule.getAction() == null) {
            return;
        }

        String type = rule.getAction().getType();
        String code = rule.getAction().getRejectionCode();
        String message = rule.getAction().getMessage();

        switch (type != null ? type.toUpperCase() : "") {
            case "REJECT":
                drl.append("    $claim.addRejection(\"")
                   .append(escapeQuotes(code != null ? code : ""))
                   .append("\", \"")
                   .append(escapeQuotes(message != null ? message : ""))
                   .append("\");\n");
                break;
            case "FLAG_FOR_REVIEW":
                drl.append("    $claim.addFlag(\"")
                   .append(escapeQuotes(message != null ? message : ""))
                   .append("\");\n");
                break;
            case "WARN":
                drl.append("    $claim.addWarning(\"")
                   .append(escapeQuotes(message != null ? message : ""))
                   .append("\");\n");
                break;
            case "CAP_TO_TARIFF":
                drl.append("    $detail.setApprovedAmount($detail.getTariffAmount());\n");
                break;
            default:
                // Unknown action type — no-op
                break;
        }
    }

    // --- Helper methods ---

    private String getFactType(String field) {
        int dot = field.indexOf('.');
        return dot > 0 ? field.substring(0, dot) : field;
    }

    private String getProperty(String field) {
        int dot = field.indexOf('.');
        return dot > 0 ? field.substring(dot + 1) : field;
    }

    private String mapOperator(String operator) {
        if (operator == null) {
            return "==";
        }
        try {
            return Operator.valueOf(operator.toUpperCase()).toDrl();
        } catch (IllegalArgumentException e) {
            // If it's already a DRL operator (e.g. "=="), pass through
            return operator;
        }
    }

    /**
     * Formats a condition value for DRL.
     * Strings are quoted; numbers and booleans are left unquoted.
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        String str = value.toString();

        // Boolean
        if ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str)) {
            return str.toLowerCase();
        }

        // Number (integer or decimal)
        if (isNumeric(str)) {
            return str;
        }

        // String — wrap in quotes
        return "\"" + escapeQuotes(str) + "\"";
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String escapeQuotes(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Internal mapping from fact type prefix to Drools variable name and class.
     */
    private static class FactMapping {
        final String variable;
        final String className;

        FactMapping(String variable, String className) {
            this.variable = variable;
            this.className = className;
        }
    }
}
