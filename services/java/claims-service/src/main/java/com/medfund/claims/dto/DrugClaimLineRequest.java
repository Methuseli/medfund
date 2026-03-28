package com.medfund.claims.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DrugClaimLineRequest(
    @NotBlank String drugCode,        // NAPPI or tariff code for drug
    @NotBlank String drugName,
    @NotNull Integer quantity,
    @NotNull BigDecimal unitPrice,
    @NotNull BigDecimal claimedAmount,
    String dosage,
    Integer daysSupply,
    String currencyCode
) {}
