package com.medfund.rules.fact;

import java.math.BigDecimal;

/**
 * Fact object representing a member inserted into the Drools KieSession for rule evaluation.
 * This is a plain POJO — not a JPA entity.
 */
public class MemberFact {

    private String memberId;
    private String status;
    private String memberType;
    private String contributionStatus;
    private int gracePeriodDays;
    private int arrearsMonths;
    private int daysSinceEnrollment;
    private int daysSinceSchemeChange;
    private boolean hasWaiver;
    private String gender;
    private int age;
    private BigDecimal benefitUsedYTD;
    private BigDecimal benefitLimit;
    private BigDecimal benefitRemaining;
    private BigDecimal absoluteLimit;
    private BigDecimal absoluteUsedYTD;
    private int claimCountYTD;

    public MemberFact() {
    }

    // --- Getters and Setters ---

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getContributionStatus() {
        return contributionStatus;
    }

    public void setContributionStatus(String contributionStatus) {
        this.contributionStatus = contributionStatus;
    }

    public int getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(int gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public int getArrearsMonths() {
        return arrearsMonths;
    }

    public void setArrearsMonths(int arrearsMonths) {
        this.arrearsMonths = arrearsMonths;
    }

    public int getDaysSinceEnrollment() {
        return daysSinceEnrollment;
    }

    public void setDaysSinceEnrollment(int daysSinceEnrollment) {
        this.daysSinceEnrollment = daysSinceEnrollment;
    }

    public int getDaysSinceSchemeChange() {
        return daysSinceSchemeChange;
    }

    public void setDaysSinceSchemeChange(int daysSinceSchemeChange) {
        this.daysSinceSchemeChange = daysSinceSchemeChange;
    }

    public boolean isHasWaiver() {
        return hasWaiver;
    }

    public void setHasWaiver(boolean hasWaiver) {
        this.hasWaiver = hasWaiver;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BigDecimal getBenefitUsedYTD() {
        return benefitUsedYTD;
    }

    public void setBenefitUsedYTD(BigDecimal benefitUsedYTD) {
        this.benefitUsedYTD = benefitUsedYTD;
    }

    public BigDecimal getBenefitLimit() {
        return benefitLimit;
    }

    public void setBenefitLimit(BigDecimal benefitLimit) {
        this.benefitLimit = benefitLimit;
    }

    public BigDecimal getBenefitRemaining() {
        return benefitRemaining;
    }

    public void setBenefitRemaining(BigDecimal benefitRemaining) {
        this.benefitRemaining = benefitRemaining;
    }

    public BigDecimal getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(BigDecimal absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }

    public BigDecimal getAbsoluteUsedYTD() {
        return absoluteUsedYTD;
    }

    public void setAbsoluteUsedYTD(BigDecimal absoluteUsedYTD) {
        this.absoluteUsedYTD = absoluteUsedYTD;
    }

    public int getClaimCountYTD() {
        return claimCountYTD;
    }

    public void setClaimCountYTD(int claimCountYTD) {
        this.claimCountYTD = claimCountYTD;
    }
}
