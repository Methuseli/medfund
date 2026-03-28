package com.medfund.user.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("waiting_period_rules")
public class WaitingPeriodRule {

    @Id
    private UUID id;

    @Column("scheme_id")
    private UUID schemeId;

    @Column("condition_type")
    private String conditionType;

    @Column("waiting_days")
    private Integer waitingDays;

    private String description;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSchemeId() { return schemeId; }
    public void setSchemeId(UUID schemeId) { this.schemeId = schemeId; }

    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }

    public Integer getWaitingDays() { return waitingDays; }
    public void setWaitingDays(Integer waitingDays) { this.waitingDays = waitingDays; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
