package com.medfund.contributions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordTransactionRequest(
        UUID contributionId,
        UUID invoiceId,
        @NotNull BigDecimal amount,
        @NotBlank String currencyCode,
        @NotBlank String transactionType,
        @NotBlank String paymentMethod,
        String reference
) {}
