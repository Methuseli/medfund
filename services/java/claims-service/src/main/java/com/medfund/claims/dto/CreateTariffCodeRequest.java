package com.medfund.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTariffCodeRequest(
        @NotNull UUID scheduleId,
        @NotBlank String code,
        @NotBlank String description,
        String category,
        @NotNull BigDecimal unitPrice,
        String currencyCode,
        Boolean requiresPreAuth
) {
    public CreateTariffCodeRequest {
        if (currencyCode == null || currencyCode.isBlank()) {
            currencyCode = "USD";
        }
    }
}
