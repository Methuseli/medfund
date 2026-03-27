package com.medfund.tenancy.dto;

import com.medfund.tenancy.entity.Tenant;

import java.time.Instant;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String name,
        String slug,
        String domain,
        String schemaName,
        UUID planId,
        String status,
        String settings,
        String branding,
        String contactEmail,
        String countryCode,
        String timezone,
        String membershipModel,
        String keycloakRealm,
        Instant createdAt,
        Instant updatedAt
) {
    public static TenantResponse from(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.getDomain(),
                tenant.getSchemaName(),
                tenant.getPlanId(),
                tenant.getStatus(),
                tenant.getSettings(),
                tenant.getBranding(),
                tenant.getContactEmail(),
                tenant.getCountryCode(),
                tenant.getTimezone(),
                tenant.getMembershipModel(),
                tenant.getKeycloakRealm(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }
}
