package com.medfund.contributions.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record GenerateBillingRequest(
        @NotNull UUID schemeId,
        UUID groupId,
        @NotNull LocalDate periodStart,
        @NotNull LocalDate periodEnd,
        String currencyCode
) {
    public GenerateBillingRequest {
        if (currencyCode == null || currencyCode.isBlank()) {
            currencyCode = "USD";
        }
    }
}
