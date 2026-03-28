package com.medfund.contributions.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;

@Service
public class ContributionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ContributionEventPublisher.class);

    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    public ContributionEventPublisher(KafkaSender<String, String> kafkaSender, ObjectMapper objectMapper) {
        this.kafkaSender = kafkaSender;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> publishBillingGenerated(String schemeId, String periodStart, String periodEnd, int count) {
        return publishEvent("medfund.contributions.billing-generated", schemeId, Map.of(
            "event", "BILLING_GENERATED",
            "schemeId", schemeId,
            "periodStart", periodStart,
            "periodEnd", periodEnd,
            "count", String.valueOf(count)
        ));
    }

    public Mono<Void> publishContributionPaid(String contributionId, String memberId, String amount) {
        return publishEvent("medfund.contributions.paid", contributionId, Map.of(
            "event", "CONTRIBUTION_PAID",
            "contributionId", contributionId,
            "memberId", memberId,
            "amount", amount
        ));
    }

    public Mono<Void> publishInvoiceIssued(String invoiceId, String invoiceNumber, String groupId) {
        return publishEvent("medfund.contributions.invoice-issued", invoiceId, Map.of(
            "event", "INVOICE_ISSUED",
            "invoiceId", invoiceId,
            "invoiceNumber", invoiceNumber,
            "groupId", groupId
        ));
    }

    private Mono<Void> publishEvent(String topic, String key, Map<String, String> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            var record = new ProducerRecord<>(topic, key, json);
            var senderRecord = SenderRecord.create(record, key);
            return kafkaSender.send(Mono.just(senderRecord))
                .doOnError(e -> log.error("Failed to publish event to {}: {}", topic, e.getMessage()))
                .then();
        } catch (Exception e) {
            log.error("Failed to serialize event for {}: {}", topic, e.getMessage());
            return Mono.empty();
        }
    }
}
