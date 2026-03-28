package com.medfund.claims.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates verification codes for claims.
 * Validation logic lives in ClaimService where the claim entity is available.
 */
@Service
public class VerificationService {

    /**
     * Generates a 6-digit random numeric verification code.
     *
     * @return a zero-padded 6-digit string
     */
    public String generateCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.valueOf(code);
    }
}
