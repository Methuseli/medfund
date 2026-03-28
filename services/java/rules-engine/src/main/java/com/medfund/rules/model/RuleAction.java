package com.medfund.rules.model;

import java.math.BigDecimal;

/**
 * Represents the action to take when a rule's conditions are satisfied.
 * Part of a RuleDefinition.
 */
public class RuleAction {

    private String type;
    private String rejectionCode;
    private String message;
    private BigDecimal adjustedAmount;

    public RuleAction() {
    }

    // --- Getters and Setters ---

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRejectionCode() {
        return rejectionCode;
    }

    public void setRejectionCode(String rejectionCode) {
        this.rejectionCode = rejectionCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getAdjustedAmount() {
        return adjustedAmount;
    }

    public void setAdjustedAmount(BigDecimal adjustedAmount) {
        this.adjustedAmount = adjustedAmount;
    }
}
