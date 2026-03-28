package com.medfund.contributions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateSchemeRequest(
        @NotBlank @Size(max = 200) String name,
        String description,
        String schemeType,
        @NotNull LocalDate effectiveDate,
        LocalDate endDate
) {
    public String schemeTypeOrDefault() {
        return (schemeType == null || schemeType.isBlank()) ? "medical_aid" : schemeType;
    }
}
