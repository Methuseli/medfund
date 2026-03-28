package com.medfund.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ClaimLineRequest(
        @NotBlank String tariffCode,
        String description,
        @NotNull Integer quantity,
        @NotNull BigDecimal unitPrice,
        @NotNull BigDecimal claimedAmount,
        String modifierCodes,
        String currencyCode
) {
}
