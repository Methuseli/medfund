package com.medfund.claims.exception;

public class DuplicateClaimException extends RuntimeException {

    public DuplicateClaimException(String claimNumber) {
        super("Claim already exists: " + claimNumber);
    }
}
