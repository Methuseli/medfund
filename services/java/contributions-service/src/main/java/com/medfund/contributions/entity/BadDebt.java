package com.medfund.contributions.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table("bad_debts")
public class BadDebt {

    @Id
    private UUID id;

    @Column("contribution_id")
    private UUID contributionId;

    @Column("member_id")
    private UUID memberId;

    @Column("group_id")
    private UUID groupId;

    private BigDecimal amount;

    @Column("currency_code")
    private String currencyCode;

    private String status = "FLAGGED";

    private String reason;

    @Column("flagged_date")
    private LocalDate flaggedDate;

    @Column("written_off_date")
    private LocalDate writtenOffDate;

    @Column("written_off_by")
    private UUID writtenOffBy;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getContributionId() { return contributionId; }
    public void setContributionId(UUID contributionId) { this.contributionId = contributionId; }

    public UUID getMemberId() { return memberId; }
    public void setMemberId(UUID memberId) { this.memberId = memberId; }

    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getFlaggedDate() { return flaggedDate; }
    public void setFlaggedDate(LocalDate flaggedDate) { this.flaggedDate = flaggedDate; }

    public LocalDate getWrittenOffDate() { return writtenOffDate; }
    public void setWrittenOffDate(LocalDate writtenOffDate) { this.writtenOffDate = writtenOffDate; }

    public UUID getWrittenOffBy() { return writtenOffBy; }
    public void setWrittenOffBy(UUID writtenOffBy) { this.writtenOffBy = writtenOffBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
