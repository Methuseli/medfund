package com.medfund.rules.fact;

import java.math.BigDecimal;

/**
 * Represents the outcome of a single rule evaluation. Collected in ClaimFact.results
 * during Drools session execution.
 * This is a plain POJO — not a JPA entity.
 */
public class RuleResult {

    private String type;
    private String code;
    private String message;
    private BigDecimal adjustedAmount;

    public RuleResult() {
    }

    public RuleResult(String type, String code, String message) {
        this.type = type;
        this.code = code;
        this.message = message;
    }

    public RuleResult(String type, String code, String message, BigDecimal adjustedAmount) {
        this.type = type;
        this.code = code;
        this.message = message;
        this.adjustedAmount = adjustedAmount;
    }

    // --- Getters and Setters ---

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
