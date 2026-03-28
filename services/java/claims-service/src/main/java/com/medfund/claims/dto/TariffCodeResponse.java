package com.medfund.claims.dto;

import com.medfund.claims.entity.TariffCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TariffCodeResponse(
        UUID id,
        UUID scheduleId,
        String code,
        String description,
        String category,
        BigDecimal unitPrice,
        String currencyCode,
        Boolean requiresPreAuth,
        Instant createdAt
) {
    public static TariffCodeResponse from(TariffCode tariffCode) {
        return new TariffCodeResponse(
                tariffCode.getId(),
                tariffCode.getScheduleId(),
                tariffCode.getCode(),
                tariffCode.getDescription(),
                tariffCode.getCategory(),
                tariffCode.getUnitPrice(),
                tariffCode.getCurrencyCode(),
                tariffCode.getRequiresPreAuth(),
                tariffCode.getCreatedAt()
        );
    }
}
