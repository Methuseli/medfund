package com.medfund.user.repository;

import com.medfund.user.entity.WaitingPeriodRule;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface WaitingPeriodRuleRepository extends R2dbcRepository<WaitingPeriodRule, UUID> {

    @Query("SELECT * FROM waiting_period_rules WHERE scheme_id = :schemeId ORDER BY condition_type")
    Flux<WaitingPeriodRule> findBySchemeId(UUID schemeId);
}
