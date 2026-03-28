package com.medfund.rules.model;

import java.util.List;

/**
 * Represents a group of conditions joined by a logical operator (AND/OR).
 * Used within RuleDefinition to express compound rule predicates.
 */
public class ConditionGroup {

    private String operator;
    private List<Condition> items;

    public ConditionGroup() {
    }

    // --- Getters and Setters ---

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<Condition> getItems() {
        return items;
    }

    public void setItems(List<Condition> items) {
        this.items = items;
    }
}
