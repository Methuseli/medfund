package com.medfund.rules.model;

/**
 * Represents a single condition within a ConditionGroup.
 * The field uses dot notation to reference fact properties
 * (e.g., "claim.benefitCategory", "member.daysSinceEnrollment").
 */
public class Condition {

    private String field;
    private String operator;
    private Object value;

    public Condition() {
    }

    // --- Getters and Setters ---

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
