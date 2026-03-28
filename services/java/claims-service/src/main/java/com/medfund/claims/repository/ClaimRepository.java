package com.medfund.claims.repository;

import com.medfund.claims.entity.Claim;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClaimRepository extends R2dbcRepository<Claim, UUID> {

    @Query("SELECT * FROM claims WHERE claim_number = :claimNumber")
    Mono<Claim> findByClaimNumber(String claimNumber);

    @Query("SELECT * FROM claims WHERE member_id = :memberId ORDER BY created_at DESC")
    Flux<Claim> findByMemberId(UUID memberId);

    @Query("SELECT * FROM claims WHERE provider_id = :providerId ORDER BY created_at DESC")
    Flux<Claim> findByProviderId(UUID providerId);

    @Query("SELECT * FROM claims WHERE status = :status ORDER BY created_at DESC")
    Flux<Claim> findByStatus(String status);

    @Query("SELECT * FROM claims WHERE member_id = :memberId AND status = :status")
    Flux<Claim> findByMemberIdAndStatus(UUID memberId, String status);

    @Query("SELECT * FROM claims ORDER BY created_at DESC")
    Flux<Claim> findAllOrderByCreatedAtDesc();

    @Query("SELECT EXISTS(SELECT 1 FROM claims WHERE claim_number = :claimNumber)")
    Mono<Boolean> existsByClaimNumber(String claimNumber);

    @Query("SELECT * FROM claims WHERE claim_type = :claimType ORDER BY created_at DESC")
    Flux<Claim> findByClaimType(String claimType);
}
