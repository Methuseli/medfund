package com.medfund.contributions.service;

import com.medfund.contributions.dto.RecordTransactionRequest;
import com.medfund.contributions.entity.Transaction;
import com.medfund.contributions.repository.TransactionRepository;
import com.medfund.shared.audit.AuditEvent;
import com.medfund.shared.audit.AuditPublisher;
import com.medfund.shared.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AuditPublisher auditPublisher;

    public TransactionService(TransactionRepository transactionRepository,
                              AuditPublisher auditPublisher) {
        this.transactionRepository = transactionRepository;
        this.auditPublisher = auditPublisher;
    }

    public Flux<Transaction> findByContributionId(UUID contributionId) {
        return transactionRepository.findByContributionId(contributionId);
    }

    public Flux<Transaction> findByInvoiceId(UUID invoiceId) {
        return transactionRepository.findByInvoiceId(invoiceId);
    }

    public Flux<Transaction> findAll() {
        return transactionRepository.findAllOrderByTransactionDateDesc();
    }

    @Transactional
    public Mono<Transaction> record(RecordTransactionRequest request, String actorId) {
        String transactionNumber = "TXN-" + String.format("%08d", ThreadLocalRandom.current().nextInt(0, 99999999));

        var transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setTransactionNumber(transactionNumber);
        transaction.setContributionId(request.contributionId());
        transaction.setInvoiceId(request.invoiceId());
        transaction.setAmount(request.amount());
        transaction.setCurrencyCode(request.currencyCode());
        transaction.setTransactionType(request.transactionType());
        transaction.setPaymentMethod(request.paymentMethod());
        transaction.setReference(request.reference());
        transaction.setStatus("completed");
        transaction.setTransactionDate(Instant.now());
        transaction.setCreatedAt(Instant.now());
        transaction.setCreatedBy(UUID.fromString(actorId));

        return transactionRepository.save(transaction)
            .flatMap(saved -> Mono.deferContextual(ctx -> {
                String tenantId = TenantContext.get(ctx);
                return publishAudit(tenantId, "Transaction", saved.getId().toString(), "CREATE", actorId,
                        null,
                        Map.of("transactionNumber", saved.getTransactionNumber(),
                               "status", saved.getStatus(),
                               "amount", saved.getAmount().toString(),
                               "transactionType", saved.getTransactionType(),
                               "paymentMethod", saved.getPaymentMethod()))
                    .thenReturn(saved);
            }));
    }

    // ---- Private helpers ----

    private Mono<Void> publishAudit(String tenantId, String entityType, String entityId,
                                     String action, String actorId,
                                     Map<String, Object> oldValue, Map<String, Object> newValue) {
        var event = AuditEvent.create(
            tenantId != null ? tenantId : "unknown",
            entityType,
            entityId,
            action,
            actorId,
            null,
            oldValue,
            newValue,
            new String[]{},
            UUID.randomUUID().toString()
        );
        return auditPublisher.publish(event);
    }
}
