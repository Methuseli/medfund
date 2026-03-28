package com.medfund.contributions.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record SchemeChangeRequest(
        @NotNull UUID memberId,
        @NotNull UUID fromSchemeId,
        @NotNull UUID toSchemeId,
        @NotNull LocalDate effectiveDate,
        String reason
) {}
