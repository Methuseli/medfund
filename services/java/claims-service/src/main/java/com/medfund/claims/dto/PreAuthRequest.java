package com.medfund.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PreAuthRequest(
        @NotNull UUID memberId,
        @NotNull UUID providerId,
        @NotNull UUID schemeId,
        @NotBlank String tariffCode,
        String diagnosisCode,
        @NotNull BigDecimal requestedAmount,
        @NotBlank String currencyCode,
        String notes
) {
}
