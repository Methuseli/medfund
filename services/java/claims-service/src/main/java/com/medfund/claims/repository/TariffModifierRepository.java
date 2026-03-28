package com.medfund.claims.repository;

import com.medfund.claims.entity.TariffModifier;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TariffModifierRepository extends R2dbcRepository<TariffModifier, UUID> {

    @Query("SELECT * FROM tariff_modifiers WHERE code = :code")
    Mono<TariffModifier> findByCode(String code);

    @Query("SELECT * FROM tariff_modifiers WHERE is_active = true ORDER BY code")
    Flux<TariffModifier> findAllActive();
}
