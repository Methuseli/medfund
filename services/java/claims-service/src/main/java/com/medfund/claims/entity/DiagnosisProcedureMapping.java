package com.medfund.claims.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("diagnosis_procedure_mappings")
public class DiagnosisProcedureMapping {

    @Id
    private UUID id;

    @Column("icd_code")
    private String icdCode;

    @Column("tariff_code")
    private String tariffCode;

    private String validity;

    private String notes;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getIcdCode() { return icdCode; }
    public void setIcdCode(String icdCode) { this.icdCode = icdCode; }

    public String getTariffCode() { return tariffCode; }
    public void setTariffCode(String tariffCode) { this.tariffCode = tariffCode; }

    public String getValidity() { return validity; }
    public void setValidity(String validity) { this.validity = validity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
