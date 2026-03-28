package com.medfund.claims.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SubmitClaimRequest(
        @NotNull UUID memberId,
        UUID dependantId,
        @NotNull UUID providerId,
        @NotNull UUID schemeId,
        UUID benefitId,
        String claimType,
        @NotNull LocalDate serviceDate,
        @NotNull @DecimalMin("0.01") BigDecimal claimedAmount,
        @NotBlank String currencyCode,
        String diagnosisCodes,
        String procedureCodes,
        String notes,
        List<ClaimLineRequest> lines
) {
    public SubmitClaimRequest {
        if (claimType == null || claimType.isBlank()) {
            claimType = "medical";
        }
        if (currencyCode == null || currencyCode.isBlank()) {
            currencyCode = "USD";
        }
    }
}
