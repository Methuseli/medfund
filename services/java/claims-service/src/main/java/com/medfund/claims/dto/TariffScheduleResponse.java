package com.medfund.claims.dto;

import com.medfund.claims.entity.TariffSchedule;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TariffScheduleResponse(
        UUID id,
        String name,
        LocalDate effectiveDate,
        LocalDate endDate,
        String source,
        String status,
        Instant createdAt
) {
    public static TariffScheduleResponse from(TariffSchedule schedule) {
        return new TariffScheduleResponse(
                schedule.getId(),
                schedule.getName(),
                schedule.getEffectiveDate(),
                schedule.getEndDate(),
                schedule.getSource(),
                schedule.getStatus(),
                schedule.getCreatedAt()
        );
    }
}
