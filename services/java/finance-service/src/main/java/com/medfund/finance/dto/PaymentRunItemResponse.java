package com.medfund.finance.dto;

import com.medfund.finance.entity.PaymentRunItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRunItemResponse(
        UUID id,
        UUID paymentRunId,
        UUID paymentId,
        UUID providerId,
        BigDecimal amount,
        String currencyCode,
        String status,
        Instant createdAt
) {
    public static PaymentRunItemResponse from(PaymentRunItem item) {
        return new PaymentRunItemResponse(
                item.getId(),
                item.getPaymentRunId(),
                item.getPaymentId(),
                item.getProviderId(),
                item.getAmount(),
                item.getCurrencyCode(),
                item.getStatus(),
                item.getCreatedAt()
        );
    }
}
