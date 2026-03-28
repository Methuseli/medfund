package com.medfund.finance.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("provider_balances")
public class ProviderBalance {

    @Id
    private UUID id;

    @Column("provider_id")
    private UUID providerId;

    @Column("total_claimed")
    private BigDecimal totalClaimed;

    @Column("total_approved")
    private BigDecimal totalApproved;

    @Column("total_paid")
    private BigDecimal totalPaid;

    @Column("outstanding_balance")
    private BigDecimal outstandingBalance;

    @Column("currency_code")
    private String currencyCode;

    @Column("last_updated_at")
    private Instant lastUpdatedAt;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProviderId() { return providerId; }
    public void setProviderId(UUID providerId) { this.providerId = providerId; }

    public BigDecimal getTotalClaimed() { return totalClaimed; }
    public void setTotalClaimed(BigDecimal totalClaimed) { this.totalClaimed = totalClaimed; }

    public BigDecimal getTotalApproved() { return totalApproved; }
    public void setTotalApproved(BigDecimal totalApproved) { this.totalApproved = totalApproved; }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Instant lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
