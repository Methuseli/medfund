package com.medfund.claims.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record QuotationRequest(
    @NotNull UUID memberId,
    @NotNull UUID providerId,
    @NotNull UUID schemeId,
    String diagnosisCode,
    String procedureCodes,
    @NotBlank String description,
    @NotNull @DecimalMin("0.01") BigDecimal estimatedAmount,
    String currencyCode,
    String notes
) {
    public String currencyCodeOrDefault() {
        return (currencyCode == null || currencyCode.isBlank()) ? "USD" : currencyCode;
    }
}
