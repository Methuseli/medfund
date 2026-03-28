package com.medfund.finance.service;

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
public class FinanceEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(FinanceEventPublisher.class);

    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    public FinanceEventPublisher(KafkaSender<String, String> kafkaSender, ObjectMapper objectMapper) {
        this.kafkaSender = kafkaSender;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> publishPaymentCreated(String paymentId, String providerId, String amount) {
        return publishEvent("medfund.finance.payment-created", paymentId, Map.of(
            "event", "PAYMENT_CREATED",
            "paymentId", paymentId,
            "providerId", providerId,
            "amount", amount
        ));
    }

    public Mono<Void> publishPaymentRunExecuted(String runId, String runNumber, int count) {
        return publishEvent("medfund.finance.payment-run-executed", runId, Map.of(
            "event", "PAYMENT_RUN_EXECUTED",
            "runId", runId,
            "runNumber", runNumber,
            "paymentCount", String.valueOf(count)
        ));
    }

    public Mono<Void> publishAdjustmentApplied(String adjustmentId, String type, String amount) {
        return publishEvent("medfund.finance.adjustment-applied", adjustmentId, Map.of(
            "event", "ADJUSTMENT_APPLIED",
            "adjustmentId", adjustmentId,
            "adjustmentType", type,
            "amount", amount
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
