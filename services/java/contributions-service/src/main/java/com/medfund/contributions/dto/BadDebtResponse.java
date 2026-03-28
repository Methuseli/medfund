package com.medfund.contributions.dto;

import com.medfund.contributions.entity.BadDebt;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BadDebtResponse(
        UUID id,
        UUID contributionId,
        UUID memberId,
        UUID groupId,
        BigDecimal amount,
        String currencyCode,
        String status,
        String reason,
        LocalDate flaggedDate,
        LocalDate writtenOffDate,
        UUID writtenOffBy,
        Instant createdAt,
        Instant updatedAt
) {
    public static BadDebtResponse from(BadDebt bd) {
        return new BadDebtResponse(
                bd.getId(),
                bd.getContributionId(),
                bd.getMemberId(),
                bd.getGroupId(),
                bd.getAmount(),
                bd.getCurrencyCode(),
                bd.getStatus(),
                bd.getReason(),
                bd.getFlaggedDate(),
                bd.getWrittenOffDate(),
                bd.getWrittenOffBy(),
                bd.getCreatedAt(),
                bd.getUpdatedAt()
        );
    }
}
