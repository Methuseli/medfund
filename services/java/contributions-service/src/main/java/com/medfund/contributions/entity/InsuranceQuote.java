package com.medfund.contributions.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table("insurance_quotes")
public class InsuranceQuote {
    @Id private UUID id;
    @Column("quote_number") private String quoteNumber;
    @Column("scheme_id") private UUID schemeId;
    @Column("first_name") private String firstName;
    @Column("last_name") private String lastName;
    @Column("date_of_birth") private LocalDate dateOfBirth;
    private String email;
    private String phone;
    @Column("dependant_count") private Integer dependantCount;
    @Column("member_premium") private BigDecimal memberPremium;
    @Column("total_premium") private BigDecimal totalPremium;
    @Column("currency_code") private String currencyCode;
    @Column("quote_details") private String quoteDetails; // JSON with full breakdown
    private String status;
    @Column("valid_until") private LocalDate validUntil;
    @CreatedDate @Column("created_at") private Instant createdAt;

    // ALL getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getQuoteNumber() { return quoteNumber; }
    public void setQuoteNumber(String quoteNumber) { this.quoteNumber = quoteNumber; }
    public UUID getSchemeId() { return schemeId; }
    public void setSchemeId(UUID schemeId) { this.schemeId = schemeId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getDependantCount() { return dependantCount; }
    public void setDependantCount(Integer dependantCount) { this.dependantCount = dependantCount; }
    public BigDecimal getMemberPremium() { return memberPremium; }
    public void setMemberPremium(BigDecimal memberPremium) { this.memberPremium = memberPremium; }
    public BigDecimal getTotalPremium() { return totalPremium; }
    public void setTotalPremium(BigDecimal totalPremium) { this.totalPremium = totalPremium; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public String getQuoteDetails() { return quoteDetails; }
    public void setQuoteDetails(String quoteDetails) { this.quoteDetails = quoteDetails; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
