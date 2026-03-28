package com.medfund.claims.repository;

import com.medfund.claims.entity.Quotation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface QuotationRepository extends R2dbcRepository<Quotation, UUID> {

    @Query("SELECT * FROM quotations WHERE quotation_number = :quotationNumber")
    Mono<Quotation> findByQuotationNumber(String quotationNumber);

    @Query("SELECT * FROM quotations WHERE member_id = :memberId ORDER BY created_at DESC")
    Flux<Quotation> findByMemberId(UUID memberId);

    @Query("SELECT * FROM quotations WHERE provider_id = :providerId ORDER BY created_at DESC")
    Flux<Quotation> findByProviderId(UUID providerId);

    @Query("SELECT * FROM quotations WHERE status = :status ORDER BY created_at DESC")
    Flux<Quotation> findByStatus(String status);

    @Query("SELECT EXISTS(SELECT 1 FROM quotations WHERE quotation_number = :quotationNumber)")
    Mono<Boolean> existsByQuotationNumber(String quotationNumber);
}
