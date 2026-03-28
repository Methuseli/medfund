package com.medfund.finance.dto;

import com.medfund.finance.entity.PaymentRun;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRunResponse(
        UUID id,
        String runNumber,
        String status,
        BigDecimal totalAmount,
        String currencyCode,
        Integer paymentCount,
        String description,
        Instant executedAt,
        UUID executedBy,
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy
) {
    public static PaymentRunResponse from(PaymentRun run) {
        return new PaymentRunResponse(
                run.getId(),
                run.getRunNumber(),
                run.getStatus(),
                run.getTotalAmount(),
                run.getCurrencyCode(),
                run.getPaymentCount(),
                run.getDescription(),
                run.getExecutedAt(),
                run.getExecutedBy(),
                run.getCreatedAt(),
                run.getUpdatedAt(),
                run.getCreatedBy()
        );
    }
}
