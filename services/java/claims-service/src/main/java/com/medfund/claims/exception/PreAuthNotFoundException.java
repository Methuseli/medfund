package com.medfund.claims.exception;

import java.util.UUID;

public class PreAuthNotFoundException extends RuntimeException {

    public PreAuthNotFoundException(UUID id) {
        super("Pre-authorization not found: " + id);
    }
}
