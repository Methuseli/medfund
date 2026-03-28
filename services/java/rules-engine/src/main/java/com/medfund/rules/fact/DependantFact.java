package com.medfund.rules.fact;

/**
 * Fact object representing a dependant inserted into the Drools KieSession for rule evaluation.
 * This is a plain POJO — not a JPA entity.
 */
public class DependantFact {

    private String dependantId;
    private String status;
    private int age;
    private int maxAge;

    public DependantFact() {
    }

    // --- Getters and Setters ---

    public String getDependantId() {
        return dependantId;
    }

    public void setDependantId(String dependantId) {
        this.dependantId = dependantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }
}
