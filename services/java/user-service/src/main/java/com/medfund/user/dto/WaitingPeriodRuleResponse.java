package com.medfund.user.dto;

import com.medfund.user.entity.WaitingPeriodRule;

import java.time.Instant;
import java.util.UUID;

public record WaitingPeriodRuleResponse(
    UUID id,
    UUID schemeId,
    String conditionType,
    Integer waitingDays,
    String description,
    Instant createdAt
) {
    public static WaitingPeriodRuleResponse from(WaitingPeriodRule w) {
        return new WaitingPeriodRuleResponse(
            w.getId(), w.getSchemeId(), w.getConditionType(),
            w.getWaitingDays(), w.getDescription(), w.getCreatedAt()
        );
    }
}
