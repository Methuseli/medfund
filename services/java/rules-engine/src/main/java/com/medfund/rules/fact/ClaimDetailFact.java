package com.medfund.rules.fact;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fact object representing a single claim line/detail inserted into the Drools KieSession.
 * This is a plain POJO — not a JPA entity.
 */
public class ClaimDetailFact {

    private String tariffCode;
    private BigDecimal billedAmount;
    private BigDecimal tariffAmount;
    private List<String> modifiers;
    private String providerSpecialty;
    private List<String> tariffAllowedSpecialties;
    private int procedureRank;
    private BigDecimal approvedAmount;

    public ClaimDetailFact() {
    }

    // --- Getters and Setters ---

    public String getTariffCode() {
        return tariffCode;
    }

    public void setTariffCode(String tariffCode) {
        this.tariffCode = tariffCode;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public BigDecimal getTariffAmount() {
        return tariffAmount;
    }

    public void setTariffAmount(BigDecimal tariffAmount) {
        this.tariffAmount = tariffAmount;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    public String getProviderSpecialty() {
        return providerSpecialty;
    }

    public void setProviderSpecialty(String providerSpecialty) {
        this.providerSpecialty = providerSpecialty;
    }

    public List<String> getTariffAllowedSpecialties() {
        return tariffAllowedSpecialties;
    }

    public void setTariffAllowedSpecialties(List<String> tariffAllowedSpecialties) {
        this.tariffAllowedSpecialties = tariffAllowedSpecialties;
    }

    public int getProcedureRank() {
        return procedureRank;
    }

    public void setProcedureRank(int procedureRank) {
        this.procedureRank = procedureRank;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }
}
