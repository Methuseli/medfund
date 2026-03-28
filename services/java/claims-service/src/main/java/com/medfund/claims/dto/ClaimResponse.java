package com.medfund.claims.dto;

import com.medfund.claims.entity.Claim;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClaimResponse(
        UUID id,
        String claimNumber,
        UUID memberId,
        UUID dependantId,
        UUID providerId,
        UUID schemeId,
        UUID benefitId,
        String claimType,
        String status,
        LocalDate serviceDate,
        Instant submissionDate,
        BigDecimal claimedAmount,
        BigDecimal approvedAmount,
        BigDecimal paidAmount,
        String currencyCode,
        String diagnosisCodes,
        String procedureCodes,
        String notes,
        String rejectionReason,
        String rejectionNotes,
        String verificationCode,
        Instant verifiedAt,
        Instant adjudicatedAt,
        UUID adjudicatedBy,
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy,
        UUID updatedBy
) {
    public static ClaimResponse from(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getClaimNumber(),
                claim.getMemberId(),
                claim.getDependantId(),
                claim.getProviderId(),
                claim.getSchemeId(),
                claim.getBenefitId(),
                claim.getClaimType(),
                claim.getStatus(),
                claim.getServiceDate(),
                claim.getSubmissionDate(),
                claim.getClaimedAmount(),
                claim.getApprovedAmount(),
                claim.getPaidAmount(),
                claim.getCurrencyCode(),
                claim.getDiagnosisCodes(),
                claim.getProcedureCodes(),
                claim.getNotes(),
                claim.getRejectionReason(),
                claim.getRejectionNotes(),
                claim.getVerificationCode(),
                claim.getVerifiedAt(),
                claim.getAdjudicatedAt(),
                claim.getAdjudicatedBy(),
                claim.getCreatedAt(),
                claim.getUpdatedAt(),
                claim.getCreatedBy(),
                claim.getUpdatedBy()
        );
    }
}
