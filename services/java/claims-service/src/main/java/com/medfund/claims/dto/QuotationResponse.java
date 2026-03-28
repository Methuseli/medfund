package com.medfund.claims.dto;

import com.medfund.claims.entity.Quotation;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record QuotationResponse(
    UUID id,
    String quotationNumber,
    UUID memberId,
    UUID providerId,
    UUID schemeId,
    String diagnosisCode,
    String procedureCodes,
    String description,
    BigDecimal estimatedAmount,
    BigDecimal coveredAmount,
    BigDecimal coPaymentAmount,
    String currencyCode,
    String status,
    LocalDate validUntil,
    String notes,
    String rejectionReason,
    Instant createdAt,
    Instant updatedAt
) {
    public static QuotationResponse from(Quotation q) {
        return new QuotationResponse(
            q.getId(), q.getQuotationNumber(), q.getMemberId(), q.getProviderId(),
            q.getSchemeId(), q.getDiagnosisCode(), q.getProcedureCodes(),
            q.getDescription(), q.getEstimatedAmount(), q.getCoveredAmount(),
            q.getCoPaymentAmount(), q.getCurrencyCode(), q.getStatus(),
            q.getValidUntil(), q.getNotes(), q.getRejectionReason(),
            q.getCreatedAt(), q.getUpdatedAt()
        );
    }
}
