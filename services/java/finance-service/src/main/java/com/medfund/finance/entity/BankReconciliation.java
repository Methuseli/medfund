package com.medfund.finance.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table("bank_reconciliations")
public class BankReconciliation {

    @Id
    private UUID id;

    @Column("reference_number")
    private String referenceNumber;

    @Column("statement_amount")
    private BigDecimal statementAmount;

    @Column("system_amount")
    private BigDecimal systemAmount;

    private BigDecimal difference;

    @Column("currency_code")
    private String currencyCode;

    private String status = "unmatched";

    private String notes;

    @Column("statement_date")
    private LocalDate statementDate;

    @Column("reconciled_at")
    private Instant reconciledAt;

    @Column("reconciled_by")
    private UUID reconciledBy;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @Column("created_by")
    private UUID createdBy;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public BigDecimal getStatementAmount() { return statementAmount; }
    public void setStatementAmount(BigDecimal statementAmount) { this.statementAmount = statementAmount; }

    public BigDecimal getSystemAmount() { return systemAmount; }
    public void setSystemAmount(BigDecimal systemAmount) { this.systemAmount = systemAmount; }

    public BigDecimal getDifference() { return difference; }
    public void setDifference(BigDecimal difference) { this.difference = difference; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getStatementDate() { return statementDate; }
    public void setStatementDate(LocalDate statementDate) { this.statementDate = statementDate; }

    public Instant getReconciledAt() { return reconciledAt; }
    public void setReconciledAt(Instant reconciledAt) { this.reconciledAt = reconciledAt; }

    public UUID getReconciledBy() { return reconciledBy; }
    public void setReconciledBy(UUID reconciledBy) { this.reconciledBy = reconciledBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
}
