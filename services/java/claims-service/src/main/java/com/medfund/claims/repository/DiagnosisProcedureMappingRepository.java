package com.medfund.claims.repository;

import com.medfund.claims.entity.DiagnosisProcedureMapping;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DiagnosisProcedureMappingRepository extends R2dbcRepository<DiagnosisProcedureMapping, UUID> {

    @Query("SELECT * FROM diagnosis_procedure_mappings WHERE icd_code = :icdCode")
    Flux<DiagnosisProcedureMapping> findByIcdCode(String icdCode);

    @Query("SELECT * FROM diagnosis_procedure_mappings WHERE tariff_code = :tariffCode")
    Flux<DiagnosisProcedureMapping> findByTariffCode(String tariffCode);

    @Query("SELECT * FROM diagnosis_procedure_mappings WHERE icd_code = :icdCode AND tariff_code = :tariffCode")
    Mono<DiagnosisProcedureMapping> findByIcdCodeAndTariffCode(String icdCode, String tariffCode);
}
