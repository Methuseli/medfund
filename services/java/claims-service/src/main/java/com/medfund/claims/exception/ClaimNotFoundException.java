package com.medfund.claims.exception;

import java.util.UUID;

public class ClaimNotFoundException extends RuntimeException {

    public ClaimNotFoundException(UUID id) {
        super("Claim not found: " + id);
    }

    public ClaimNotFoundException(String claimNumber) {
        super("Claim not found: " + claimNumber);
    }
}
