package com.medfund.rules.service;

import com.medfund.rules.model.Condition;
import com.medfund.rules.model.ConditionGroup;
import com.medfund.rules.model.RuleAction;
import com.medfund.rules.model.RuleDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Provides default rule templates that are seeded for new tenants.
 * <p>
 * These rules represent common healthcare claims adjudication logic
 * that most medical aid schemes require. Tenant admins can modify,
 * disable, or extend these rules after provisioning.
 */
@Service
public class RuleTemplateService {

    /**
     * Returns the full set of default rule definitions for a new tenant.
     * Rules are ordered by priority (higher salience = evaluated first).
     */
    public List<RuleDefinition> getDefaultRules() {
        List<RuleDefinition> rules = new ArrayList<>();

        // --- ELIGIBILITY rules ---

        rules.add(buildRule(
                "R01 - Member must be active",
                "ELIGIBILITY",
                100,
                conditionGroup("AND",
                        condition("member.status", "NOT_EQUALS", "ACTIVE")),
                rejectAction("R01", "Claim rejected: member is not in active status")
        ));

        rules.add(buildRule(
                "R11 - Contributions not in arrears >3 months",
                "ELIGIBILITY",
                95,
                conditionGroup("AND",
                        condition("member.arrearsMonths", "GREATER_THAN", "3")),
                rejectAction("R11", "Claim rejected: member contributions in arrears for more than 3 months")
        ));

        rules.add(buildRule(
                "R15 - Claim submitted within 90 days of service",
                "ELIGIBILITY",
                90,
                conditionGroup("AND",
                        condition("claim.daysSinceService", "GREATER_THAN", "90")),
                rejectAction("R15", "Claim rejected: submitted more than 90 days after date of service")
        ));

        // --- WAITING PERIOD rules ---

        rules.add(buildRule(
                "R02-GEN - General illness waiting period (90 days)",
                "WAITING_PERIOD",
                85,
                conditionGroup("AND",
                        condition("member.daysSinceEnrollment", "LESS_THAN", "90"),
                        condition("claim.isEmergency", "EQUALS", "false"),
                        condition("claim.isAccident", "EQUALS", "false")),
                rejectAction("R02", "Claim rejected: general illness waiting period of 90 days not met")
        ));

        rules.add(buildRule(
                "R02-MAT - Maternity waiting period (300 days)",
                "WAITING_PERIOD",
                84,
                conditionGroup("AND",
                        condition("claim.benefitCategory", "EQUALS", "MATERNITY"),
                        condition("member.daysSinceEnrollment", "LESS_THAN", "300")),
                rejectAction("R02", "Claim rejected: maternity waiting period of 300 days not met")
        ));

        rules.add(buildRule(
                "R02-DEN - Dental prosthetics waiting period (180 days)",
                "WAITING_PERIOD",
                83,
                conditionGroup("AND",
                        condition("claim.benefitCategory", "EQUALS", "DENTAL_PROSTHETICS"),
                        condition("member.daysSinceEnrollment", "LESS_THAN", "180")),
                rejectAction("R02", "Claim rejected: dental prosthetics waiting period of 180 days not met")
        ));

        // --- BENEFIT LIMIT rules ---

        rules.add(buildRule(
                "R03 - Benefit limit exhausted",
                "BENEFIT_LIMIT",
                80,
                conditionGroup("AND",
                        condition("member.benefitRemaining", "LESS_THAN_OR_EQUALS", "0")),
                rejectAction("R03", "Claim rejected: benefit limit for this category has been exhausted")
        ));

        // --- PRE-AUTHORIZATION rules ---

        rules.add(buildRule(
                "R04 - Pre-authorization required but not obtained",
                "PRE_AUTHORIZATION",
                75,
                conditionGroup("AND",
                        condition("claim.isElective", "EQUALS", "true"),
                        condition("claim.hasPreAuth", "EQUALS", "false")),
                rejectAction("R04", "Claim rejected: pre-authorization is required for elective procedures but was not obtained")
        ));

        rules.add(buildRule(
                "R05 - Pre-authorization expired",
                "PRE_AUTHORIZATION",
                74,
                conditionGroup("AND",
                        condition("claim.hasPreAuth", "EQUALS", "true"),
                        condition("claim.preAuthStatus", "EQUALS", "EXPIRED")),
                rejectAction("R05", "Claim rejected: pre-authorization has expired")
        ));

        // --- TARIFF PRICING rules ---

        rules.add(buildRule(
                "TAR01 - Cap billed amount to tariff",
                "TARIFF_PRICING",
                70,
                conditionGroup("AND",
                        condition("claimDetail.billedAmount", "GREATER_THAN", "0")),
                capToTariffAction("Billed amount capped to tariff rate")
        ));

        // --- CLINICAL VALIDATION rules ---

        rules.add(buildRule(
                "R17 - Gender-inappropriate procedure",
                "CLINICAL_VALIDATION",
                65,
                conditionGroup("AND",
                        condition("claim.benefitCategory", "EQUALS", "MATERNITY"),
                        condition("member.gender", "EQUALS", "MALE")),
                rejectAction("R17", "Claim rejected: procedure is not appropriate for the member's gender")
        ));

        rules.add(buildRule(
                "R18 - Age-inappropriate procedure (paediatric for adult)",
                "CLINICAL_VALIDATION",
                64,
                conditionGroup("AND",
                        condition("claim.benefitCategory", "EQUALS", "PAEDIATRIC"),
                        condition("member.age", "GREATER_THAN", "18")),
                rejectAction("R18", "Claim rejected: paediatric procedure is not appropriate for member's age")
        ));

        return rules;
    }

    // --- Builder helpers ---

    private RuleDefinition buildRule(String name, String category, int priority,
                                     ConditionGroup conditions, RuleAction action) {
        RuleDefinition rule = new RuleDefinition();
        rule.setId(UUID.randomUUID().toString());
        rule.setName(name);
        rule.setCategory(category);
        rule.setPriority(priority);
        rule.setStatus("ACTIVE");
        rule.setVersion(1);
        rule.setConditions(conditions);
        rule.setAction(action);
        return rule;
    }

    private ConditionGroup conditionGroup(String operator, Condition... conditions) {
        ConditionGroup group = new ConditionGroup();
        group.setOperator(operator);
        group.setItems(Arrays.asList(conditions));
        return group;
    }

    private Condition condition(String field, String operator, Object value) {
        Condition c = new Condition();
        c.setField(field);
        c.setOperator(operator);
        c.setValue(value);
        return c;
    }

    private RuleAction rejectAction(String code, String message) {
        RuleAction action = new RuleAction();
        action.setType("REJECT");
        action.setRejectionCode(code);
        action.setMessage(message);
        return action;
    }

    private RuleAction capToTariffAction(String message) {
        RuleAction action = new RuleAction();
        action.setType("CAP_TO_TARIFF");
        action.setMessage(message);
        return action;
    }
}
