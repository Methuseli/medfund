package com.medfund.claims.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SubmitDrugClaimRequest(
    @NotNull UUID memberId,
    UUID dependantId,
    @NotNull UUID providerId,  // pharmacy
    @NotNull UUID schemeId,
    UUID benefitId,
    @NotNull LocalDate serviceDate,
    @NotNull @DecimalMin("0.01") BigDecimal claimedAmount,
    String currencyCode,
    String diagnosisCodes,
    String prescriptionNumber,
    String prescribingDoctor,
    String notes,
    @NotNull List<DrugClaimLineRequest> lines
) {
    public String currencyCodeOrDefault() {
        return (currencyCode == null || currencyCode.isBlank()) ? "USD" : currencyCode;
    }
}
