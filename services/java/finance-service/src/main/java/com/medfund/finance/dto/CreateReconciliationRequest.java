package com.medfund.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateReconciliationRequest(
        @NotBlank String referenceNumber,
        @NotNull BigDecimal statementAmount,
        @NotNull BigDecimal systemAmount,
        @NotNull String currencyCode,
        @NotNull LocalDate statementDate,
        String notes
) {}
