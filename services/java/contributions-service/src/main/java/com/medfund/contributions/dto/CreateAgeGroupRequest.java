package com.medfund.contributions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAgeGroupRequest(
        @NotNull UUID schemeId,
        @NotBlank String name,
        @NotNull Integer minAge,
        @NotNull Integer maxAge,
        @NotNull BigDecimal contributionAmount,
        String currencyCode
) {
    public CreateAgeGroupRequest {
        if (currencyCode == null || currencyCode.isBlank()) {
            currencyCode = "USD";
        }
    }
}
