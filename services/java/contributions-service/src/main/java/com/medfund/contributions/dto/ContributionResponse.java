package com.medfund.contributions.dto;

import com.medfund.contributions.entity.Contribution;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ContributionResponse(
        UUID id,
        UUID memberId,
        UUID groupId,
        UUID schemeId,
        BigDecimal amount,
        String currencyCode,
        LocalDate periodStart,
        LocalDate periodEnd,
        String status,
        String paymentMethod,
        String paymentReference,
        Instant paidAt,
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy,
        UUID updatedBy
) {
    public static ContributionResponse from(Contribution contribution) {
        return new ContributionResponse(
                contribution.getId(),
                contribution.getMemberId(),
                contribution.getGroupId(),
                contribution.getSchemeId(),
                contribution.getAmount(),
                contribution.getCurrencyCode(),
                contribution.getPeriodStart(),
                contribution.getPeriodEnd(),
                contribution.getStatus(),
                contribution.getPaymentMethod(),
                contribution.getPaymentReference(),
                contribution.getPaidAt(),
                contribution.getCreatedAt(),
                contribution.getUpdatedAt(),
                contribution.getCreatedBy(),
                contribution.getUpdatedBy()
        );
    }
}
