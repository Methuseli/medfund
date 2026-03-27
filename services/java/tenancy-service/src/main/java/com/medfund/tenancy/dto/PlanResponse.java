package com.medfund.tenancy.dto;

import com.medfund.tenancy.entity.Plan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        String name,
        Integer maxMembers,
        Integer maxProviders,
        Integer maxStorageGb,
        String features,
        BigDecimal price,
        String currencyCode,
        String billingCycle,
        Boolean isActive,
        Instant createdAt
) {
    public static PlanResponse from(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getMaxMembers(),
                plan.getMaxProviders(),
                plan.getMaxStorageGb(),
                plan.getFeatures(),
                plan.getPrice(),
                plan.getCurrencyCode(),
                plan.getBillingCycle(),
                plan.getIsActive(),
                plan.getCreatedAt()
        );
    }
}
