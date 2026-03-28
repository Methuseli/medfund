package com.medfund.rules.model;

/**
 * Types of actions a rule can produce when its conditions are met.
 */
public enum ActionType {
    REJECT,
    FLAG_FOR_REVIEW,
    WARN,
    CAP_TO_TARIFF
}
