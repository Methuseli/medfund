package com.medfund.claims.repository;

import com.medfund.claims.entity.PreAuthorization;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PreAuthorizationRepository extends R2dbcRepository<PreAuthorization, UUID> {

    @Query("SELECT * FROM pre_authorizations WHERE member_id = :memberId ORDER BY created_at DESC")
    Flux<PreAuthorization> findByMemberId(UUID memberId);

    @Query("SELECT * FROM pre_authorizations WHERE auth_number = :authNumber")
    Mono<PreAuthorization> findByAuthNumber(String authNumber);

    @Query("SELECT * FROM pre_authorizations WHERE status = :status ORDER BY created_at DESC")
    Flux<PreAuthorization> findByStatus(String status);

    @Query("SELECT * FROM pre_authorizations WHERE member_id = :memberId AND tariff_code = :tariffCode AND status = :status")
    Mono<PreAuthorization> findByMemberIdAndTariffCodeAndStatus(UUID memberId, String tariffCode, String status);

    @Query("SELECT EXISTS(SELECT 1 FROM pre_authorizations WHERE auth_number = :authNumber)")
    Mono<Boolean> existsByAuthNumber(String authNumber);
}
