package com.medfund.claims.repository;

import com.medfund.claims.entity.TariffCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TariffCodeRepository extends R2dbcRepository<TariffCode, UUID> {

    @Query("SELECT * FROM tariff_codes WHERE schedule_id = :scheduleId ORDER BY code")
    Flux<TariffCode> findByScheduleId(UUID scheduleId);

    @Query("SELECT * FROM tariff_codes WHERE code = :code")
    Mono<TariffCode> findByCode(String code);

    @Query("SELECT * FROM tariff_codes WHERE schedule_id = :scheduleId AND code = :code")
    Mono<TariffCode> findByScheduleIdAndCode(UUID scheduleId, String code);

    @Query("SELECT * FROM tariff_codes WHERE LOWER(code) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Flux<TariffCode> searchByCodeOrDescription(String query);
}
