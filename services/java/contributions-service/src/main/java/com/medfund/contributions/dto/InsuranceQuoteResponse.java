package com.medfund.contributions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InsuranceQuoteResponse(
    String quoteNumber,
    String schemeName,
    String schemeType,
    LocalDate quoteDate,
    LocalDate validUntil,
    BigDecimal memberPremium,
    String memberAgeGroup,
    List<DependantPremium> dependantPremiums,
    BigDecimal totalMonthlyPremium,
    String currencyCode,
    List<BenefitSummary> benefits,
    String status  // GENERATED, SAVED, EXPIRED, CONVERTED
) {
    public record DependantPremium(
        String name,
        String relationship,
        int age,
        String ageGroup,
        BigDecimal premium
    ) {}

    public record BenefitSummary(
        String benefitName,
        String benefitType,
        BigDecimal annualLimit,
        String currencyCode
    ) {}
}
