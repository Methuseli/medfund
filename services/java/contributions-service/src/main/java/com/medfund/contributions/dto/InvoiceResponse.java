package com.medfund.contributions.dto;

import com.medfund.contributions.entity.Invoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        String invoiceNumber,
        UUID schemeId,
        UUID groupId,
        UUID memberId,
        BigDecimal totalAmount,
        String currencyCode,
        LocalDate periodStart,
        LocalDate periodEnd,
        String status,
        LocalDate dueDate,
        Instant issuedAt,
        Instant paidAt,
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy
) {
    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getSchemeId(),
                invoice.getGroupId(),
                invoice.getMemberId(),
                invoice.getTotalAmount(),
                invoice.getCurrencyCode(),
                invoice.getPeriodStart(),
                invoice.getPeriodEnd(),
                invoice.getStatus(),
                invoice.getDueDate(),
                invoice.getIssuedAt(),
                invoice.getPaidAt(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt(),
                invoice.getCreatedBy()
        );
    }
}
