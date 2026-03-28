package com.medfund.finance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID providerId,
        @NotNull @Positive BigDecimal amount,
        @NotNull String currencyCode,
        String paymentType,
        String paymentMethod,
        String reference
) {}
