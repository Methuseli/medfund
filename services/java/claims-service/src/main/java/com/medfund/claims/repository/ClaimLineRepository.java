package com.medfund.claims.repository;

import com.medfund.claims.entity.ClaimLine;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ClaimLineRepository extends R2dbcRepository<ClaimLine, UUID> {

    @Query("SELECT * FROM claim_lines WHERE claim_id = :claimId ORDER BY created_at")
    Flux<ClaimLine> findByClaimId(UUID claimId);
}
