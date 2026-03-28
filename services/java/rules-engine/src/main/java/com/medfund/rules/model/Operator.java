package com.medfund.rules.model;

/**
 * Comparison operators used in rule conditions.
 * Each maps to a Drools DRL operator.
 */
public enum Operator {
    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUALS(">="),
    LESS_THAN_OR_EQUALS("<=");

    private final String drl;

    Operator(String drl) {
        this.drl = drl;
    }

    public String toDrl() {
        return drl;
    }
}
