package com.medfund.finance.repository;

import com.medfund.finance.entity.PaymentRun;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentRunRepository extends R2dbcRepository<PaymentRun, UUID> {

    @Query("SELECT * FROM payment_runs WHERE run_number = :runNumber")
    Mono<PaymentRun> findByRunNumber(String runNumber);

    @Query("SELECT * FROM payment_runs WHERE status = :status ORDER BY created_at DESC")
    Flux<PaymentRun> findByStatus(String status);

    @Query("SELECT * FROM payment_runs ORDER BY created_at DESC")
    Flux<PaymentRun> findAllOrderByCreatedAtDesc();

    @Query("SELECT EXISTS(SELECT 1 FROM payment_runs WHERE run_number = :runNumber)")
    Mono<Boolean> existsByRunNumber(String runNumber);
}
