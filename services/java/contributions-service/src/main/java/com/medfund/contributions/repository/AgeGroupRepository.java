package com.medfund.contributions.repository;

import com.medfund.contributions.entity.AgeGroup;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface AgeGroupRepository extends R2dbcRepository<AgeGroup, UUID> {

    @Query("SELECT * FROM age_groups WHERE scheme_id = :schemeId ORDER BY min_age")
    Flux<AgeGroup> findBySchemeId(UUID schemeId);
}
