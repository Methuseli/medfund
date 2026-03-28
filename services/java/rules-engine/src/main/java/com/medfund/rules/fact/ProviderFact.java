package com.medfund.rules.fact;

/**
 * Fact object representing a healthcare provider inserted into the Drools KieSession for rule evaluation.
 * This is a plain POJO — not a JPA entity.
 */
public class ProviderFact {

    private String providerId;
    private String registrationStatus;
    private String ahfozSpecialty;

    public ProviderFact() {
    }

    // --- Getters and Setters ---

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getAhfozSpecialty() {
        return ahfozSpecialty;
    }

    public void setAhfozSpecialty(String ahfozSpecialty) {
        this.ahfozSpecialty = ahfozSpecialty;
    }
}
