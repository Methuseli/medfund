package com.medfund.contributions.repository;

import com.medfund.contributions.entity.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {

    @Query("SELECT * FROM transactions WHERE contribution_id = :contributionId")
    Flux<Transaction> findByContributionId(UUID contributionId);

    @Query("SELECT * FROM transactions WHERE invoice_id = :invoiceId")
    Flux<Transaction> findByInvoiceId(UUID invoiceId);

    @Query("SELECT * FROM transactions WHERE transaction_number = :transactionNumber")
    Mono<Transaction> findByTransactionNumber(String transactionNumber);

    @Query("SELECT * FROM transactions ORDER BY transaction_date DESC")
    Flux<Transaction> findAllOrderByTransactionDateDesc();
}
