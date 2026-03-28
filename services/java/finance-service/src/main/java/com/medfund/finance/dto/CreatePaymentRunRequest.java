package com.medfund.finance.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePaymentRunRequest(
        @NotBlank String currencyCode,
        String description
) {}
