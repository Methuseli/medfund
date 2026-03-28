package com.medfund.finance.dto;

import com.medfund.finance.entity.BankReconciliation;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BankReconciliationResponse(
        UUID id,
        String referenceNumber,
        BigDecimal statementAmount,
        BigDecimal systemAmount,
        BigDecimal difference,
        String currencyCode,
        String status,
        String notes,
        LocalDate statementDate,
        Instant reconciledAt,
        UUID reconciledBy,
        Instant createdAt,
        UUID createdBy
) {
    public static BankReconciliationResponse from(BankReconciliation recon) {
        return new BankReconciliationResponse(
                recon.getId(),
                recon.getReferenceNumber(),
                recon.getStatementAmount(),
                recon.getSystemAmount(),
                recon.getDifference(),
                recon.getCurrencyCode(),
                recon.getStatus(),
                recon.getNotes(),
                recon.getStatementDate(),
                recon.getReconciledAt(),
                recon.getReconciledBy(),
                recon.getCreatedAt(),
                recon.getCreatedBy()
        );
    }
}
