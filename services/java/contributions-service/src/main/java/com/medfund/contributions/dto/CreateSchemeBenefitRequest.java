package com.medfund.contributions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSchemeBenefitRequest(
        @NotNull UUID schemeId,
        @NotBlank String name,
        @NotBlank String benefitType,
        BigDecimal annualLimit,
        BigDecimal dailyLimit,
        BigDecimal eventLimit,
        String currencyCode,
        Integer waitingPeriodDays,
        String description
) {
    public CreateSchemeBenefitRequest {
        if (currencyCode == null || currencyCode.isBlank()) {
            currencyCode = "USD";
        }
    }
}
