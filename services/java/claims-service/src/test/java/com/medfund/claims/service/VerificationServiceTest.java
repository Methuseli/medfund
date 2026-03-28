package com.medfund.claims.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationServiceTest {

    private final VerificationService verificationService = new VerificationService();

    @Test
    void generateCode_returns6DigitString() {
        String code = verificationService.generateCode();

        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d{6}");
    }

    @Test
    void generateCode_withinRange() {
        String code = verificationService.generateCode();

        int parsed = Integer.parseInt(code);
        assertThat(parsed).isBetween(100000, 999999);
    }

    @Test
    void generateCode_producesVariation() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            codes.add(verificationService.generateCode());
        }

        assertThat(codes).hasSizeGreaterThan(1);
    }
}
