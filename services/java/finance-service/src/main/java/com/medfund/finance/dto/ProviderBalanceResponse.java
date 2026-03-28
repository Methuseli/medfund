package com.medfund.finance.dto;

import com.medfund.finance.entity.ProviderBalance;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProviderBalanceResponse(
        UUID id,
        UUID providerId,
        BigDecimal totalClaimed,
        BigDecimal totalApproved,
        BigDecimal totalPaid,
        BigDecimal outstandingBalance,
        String currencyCode,
        Instant lastUpdatedAt,
        Instant createdAt
) {
    public static ProviderBalanceResponse from(ProviderBalance balance) {
        return new ProviderBalanceResponse(
                balance.getId(),
                balance.getProviderId(),
                balance.getTotalClaimed(),
                balance.getTotalApproved(),
                balance.getTotalPaid(),
                balance.getOutstandingBalance(),
                balance.getCurrencyCode(),
                balance.getLastUpdatedAt(),
                balance.getCreatedAt()
        );
    }
}
