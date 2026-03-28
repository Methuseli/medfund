package com.medfund.finance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PaymentAdvice(
    String adviceNumber,
    UUID providerId,
    String providerName,
    BigDecimal totalAmount,
    String currencyCode,
    Instant generatedAt,
    List<PaymentAdviceLine> lines
) {
    public record PaymentAdviceLine(
        String claimNumber,
        String memberName,
        BigDecimal claimedAmount,
        BigDecimal approvedAmount,
        BigDecimal paidAmount,
        String serviceDate
    ) {}
}
