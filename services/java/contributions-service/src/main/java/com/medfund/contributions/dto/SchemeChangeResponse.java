package com.medfund.contributions.dto;

import com.medfund.contributions.entity.SchemeChange;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SchemeChangeResponse(
        UUID id,
        UUID memberId,
        UUID fromSchemeId,
        UUID toSchemeId,
        String status,
        LocalDate requestedDate,
        LocalDate effectiveDate,
        String reason,
        String rejectionReason,
        UUID approvedBy,
        Instant approvedAt,
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy
) {
    public static SchemeChangeResponse from(SchemeChange sc) {
        return new SchemeChangeResponse(
                sc.getId(),
                sc.getMemberId(),
                sc.getFromSchemeId(),
                sc.getToSchemeId(),
                sc.getStatus(),
                sc.getRequestedDate(),
                sc.getEffectiveDate(),
                sc.getReason(),
                sc.getRejectionReason(),
                sc.getApprovedBy(),
                sc.getApprovedAt(),
                sc.getCreatedAt(),
                sc.getUpdatedAt(),
                sc.getCreatedBy()
        );
    }
}
