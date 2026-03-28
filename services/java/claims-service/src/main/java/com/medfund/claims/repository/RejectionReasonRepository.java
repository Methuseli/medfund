package com.medfund.claims.repository;

import com.medfund.claims.entity.RejectionReason;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RejectionReasonRepository extends R2dbcRepository<RejectionReason, UUID> {

    @Query("SELECT * FROM rejection_reasons WHERE code = :code")
    Mono<RejectionReason> findByCode(String code);

    @Query("SELECT * FROM rejection_reasons WHERE is_active = true ORDER BY code")
    Flux<RejectionReason> findAllActive();
}
