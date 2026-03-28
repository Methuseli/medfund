package com.medfund.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateTariffScheduleRequest(
        @NotBlank String name,
        @NotNull LocalDate effectiveDate,
        LocalDate endDate,
        String source
) {
}
