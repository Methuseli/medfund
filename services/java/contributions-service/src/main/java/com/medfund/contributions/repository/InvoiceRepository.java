package com.medfund.contributions.repository;

import com.medfund.contributions.entity.Invoice;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InvoiceRepository extends R2dbcRepository<Invoice, UUID> {

    @Query("SELECT * FROM invoices WHERE invoice_number = :invoiceNumber")
    Mono<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT * FROM invoices WHERE group_id = :groupId ORDER BY created_at DESC")
    Flux<Invoice> findByGroupId(UUID groupId);

    @Query("SELECT * FROM invoices WHERE member_id = :memberId ORDER BY created_at DESC")
    Flux<Invoice> findByMemberId(UUID memberId);

    @Query("SELECT * FROM invoices WHERE status = :status ORDER BY created_at DESC")
    Flux<Invoice> findByStatus(String status);

    @Query("SELECT EXISTS(SELECT 1 FROM invoices WHERE invoice_number = :invoiceNumber)")
    Mono<Boolean> existsByInvoiceNumber(String invoiceNumber);
}
