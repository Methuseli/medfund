package com.medfund.rules.fact;

import java.math.BigDecimal;

/**
 * Fact object representing a family unit inserted into the Drools KieSession for rule evaluation.
 * This is a plain POJO — not a JPA entity.
 */
public class FamilyFact {

    private BigDecimal benefitUsedYTD;

    public FamilyFact() {
    }

    // --- Getters and Setters ---

    public BigDecimal getBenefitUsedYTD() {
        return benefitUsedYTD;
    }

    public void setBenefitUsedYTD(BigDecimal benefitUsedYTD) {
        this.benefitUsedYTD = benefitUsedYTD;
    }
}
