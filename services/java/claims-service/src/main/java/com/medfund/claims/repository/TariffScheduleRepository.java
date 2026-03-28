package com.medfund.claims.repository;

import com.medfund.claims.entity.TariffSchedule;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TariffScheduleRepository extends R2dbcRepository<TariffSchedule, UUID> {

    @Query("SELECT * FROM tariff_schedules WHERE status = :status ORDER BY effective_date DESC")
    Flux<TariffSchedule> findByStatus(String status);

    @Query("SELECT * FROM tariff_schedules ORDER BY effective_date DESC")
    Flux<TariffSchedule> findAllOrderByEffectiveDateDesc();
}
