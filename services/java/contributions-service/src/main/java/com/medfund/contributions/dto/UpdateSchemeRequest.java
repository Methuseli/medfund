package com.medfund.contributions.dto;

import java.time.LocalDate;

public record UpdateSchemeRequest(
        String name,
        String description,
        String schemeType,
        LocalDate endDate
) {}
