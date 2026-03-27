package com.medfund.tenancy.exception;

public class TenantSlugConflictException extends RuntimeException {
    public TenantSlugConflictException(String slug) {
        super("Tenant slug already exists: " + slug);
    }
}
