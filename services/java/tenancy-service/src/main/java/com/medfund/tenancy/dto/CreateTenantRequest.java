package com.medfund.tenancy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTenantRequest(
        @NotBlank @Size(max = 200)
        String name,

        @NotBlank @Size(max = 100) @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase alphanumeric with hyphens")
        String slug,

        @Size(max = 255)
        String domain,

        UUID planId,

        @NotBlank @Email
        String contactEmail,

        @NotBlank @Size(min = 2, max = 2)
        String countryCode,

        String timezone,

        @Pattern(regexp = "^(GROUP_ONLY|INDIVIDUAL_ONLY|BOTH)$", message = "Must be GROUP_ONLY, INDIVIDUAL_ONLY, or BOTH")
        String membershipModel,

        String defaultCurrencyCode
) {
    public String timezoneOrDefault() {
        return (timezone == null || timezone.isBlank()) ? "UTC" : timezone;
    }

    public String membershipModelOrDefault() {
        return (membershipModel == null || membershipModel.isBlank()) ? "BOTH" : membershipModel;
    }

    public String defaultCurrencyCodeOrDefault() {
        return (defaultCurrencyCode == null || defaultCurrencyCode.isBlank()) ? "USD" : defaultCurrencyCode;
    }
}
