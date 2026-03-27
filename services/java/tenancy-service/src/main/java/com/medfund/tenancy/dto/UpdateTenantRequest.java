package com.medfund.tenancy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateTenantRequest(
        @Size(max = 200)
        String name,

        @Size(max = 255)
        String domain,

        UUID planId,

        @Email
        String contactEmail,

        String timezone,

        String membershipModel,

        String settings,

        String branding
) {}
