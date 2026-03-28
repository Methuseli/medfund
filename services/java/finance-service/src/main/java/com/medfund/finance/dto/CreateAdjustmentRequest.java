package com.medfund.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAdjustmentRequest(
        @NotNull UUID providerId,
        UUID memberId,
        @NotBlank String adjustmentType,
        @NotNull @Positive BigDecimal amount,
        @NotNull String currencyCode,
        String reason
) {}
