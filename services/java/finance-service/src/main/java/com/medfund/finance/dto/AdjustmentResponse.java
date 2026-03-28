package com.medfund.finance.dto;

import com.medfund.finance.entity.Adjustment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdjustmentResponse(
        UUID id,
        String adjustmentNumber,
        UUID providerId,
        UUID memberId,
        String adjustmentType,
        BigDecimal amount,
        String currencyCode,
        String reason,
        String status,
        UUID approvedBy,
        Instant approvedAt,
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy
) {
    public static AdjustmentResponse from(Adjustment adj) {
        return new AdjustmentResponse(
                adj.getId(),
                adj.getAdjustmentNumber(),
                adj.getProviderId(),
                adj.getMemberId(),
                adj.getAdjustmentType(),
                adj.getAmount(),
                adj.getCurrencyCode(),
                adj.getReason(),
                adj.getStatus(),
                adj.getApprovedBy(),
                adj.getApprovedAt(),
                adj.getCreatedAt(),
                adj.getUpdatedAt(),
                adj.getCreatedBy()
        );
    }
}
