package com.medfund.finance.repository;

import com.medfund.finance.entity.BankReconciliation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BankReconciliationRepository extends R2dbcRepository<BankReconciliation, UUID> {

    @Query("SELECT * FROM bank_reconciliations WHERE status = :status ORDER BY created_at DESC")
    Flux<BankReconciliation> findByStatus(String status);

    @Query("SELECT * FROM bank_reconciliations ORDER BY created_at DESC")
    Flux<BankReconciliation> findAllOrderByCreatedAtDesc();
}
