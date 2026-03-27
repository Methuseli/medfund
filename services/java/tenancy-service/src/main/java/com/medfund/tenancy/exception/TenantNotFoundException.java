package com.medfund.tenancy.exception;

import java.util.UUID;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(UUID id) {
        super("Tenant not found: " + id);
    }

    public TenantNotFoundException(String slug) {
        super("Tenant not found with slug: " + slug);
    }
}
