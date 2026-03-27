package com.medfund.tenancy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePlanRequest(
        @NotBlank @Size(max = 100)
        String name,

        Integer maxMembers,
        Integer maxProviders,
        Integer maxStorageGb,
        String features,
        BigDecimal price,

        @Size(min = 3, max = 3)
        String currencyCode,

        String billingCycle
) {}
