package com.medfund.finance.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("adjustments")
public class Adjustment {

    @Id
    private UUID id;

    @Column("adjustment_number")
    private String adjustmentNumber;

    @Column("provider_id")
    private UUID providerId;

    @Column("member_id")
    private UUID memberId;

    @Column("adjustment_type")
    private String adjustmentType;

    private BigDecimal amount;

    @Column("currency_code")
    private String currencyCode;

    private String reason;

    private String status = "pending";

    @Column("approved_by")
    private UUID approvedBy;

    @Column("approved_at")
    private Instant approvedAt;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    @Column("created_by")
    private UUID createdBy;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getAdjustmentNumber() { return adjustmentNumber; }
    public void setAdjustmentNumber(String adjustmentNumber) { this.adjustmentNumber = adjustmentNumber; }

    public UUID getProviderId() { return providerId; }
    public void setProviderId(UUID providerId) { this.providerId = providerId; }

    public UUID getMemberId() { return memberId; }
    public void setMemberId(UUID memberId) { this.memberId = memberId; }

    public String getAdjustmentType() { return adjustmentType; }
    public void setAdjustmentType(String adjustmentType) { this.adjustmentType = adjustmentType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getApprovedBy() { return approvedBy; }
    public void setApprovedBy(UUID approvedBy) { this.approvedBy = approvedBy; }

    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
}
