package com.medfund.contributions.repository;

import com.medfund.contributions.entity.InsuranceQuote;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface InsuranceQuoteRepository extends R2dbcRepository<InsuranceQuote, UUID> {
    @Query("SELECT * FROM insurance_quotes WHERE quote_number = :quoteNumber")
    Mono<InsuranceQuote> findByQuoteNumber(String quoteNumber);

    @Query("SELECT * FROM insurance_quotes WHERE email = :email ORDER BY created_at DESC")
    Flux<InsuranceQuote> findByEmail(String email);

    @Query("SELECT EXISTS(SELECT 1 FROM insurance_quotes WHERE quote_number = :quoteNumber)")
    Mono<Boolean> existsByQuoteNumber(String quoteNumber);
}
