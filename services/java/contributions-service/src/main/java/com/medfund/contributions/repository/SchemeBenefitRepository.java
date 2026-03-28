package com.medfund.contributions.repository;

import com.medfund.contributions.entity.SchemeBenefit;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SchemeBenefitRepository extends R2dbcRepository<SchemeBenefit, UUID> {

    @Query("SELECT * FROM scheme_benefits WHERE scheme_id = :schemeId ORDER BY name")
    Flux<SchemeBenefit> findBySchemeId(UUID schemeId);
}
