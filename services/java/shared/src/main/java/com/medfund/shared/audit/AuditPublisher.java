package com.medfund.shared.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Publishes audit events to Kafka topic for consumption by the Audit Service.
 * All entity mutations must call this — see coding-standards.md.
 */
@Component
public class AuditPublisher {

    private static final Logger log = LoggerFactory.getLogger(AuditPublisher.class);
    private static final String TOPIC = "medfund.audit.events";

    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    public AuditPublisher(KafkaSender<String, String> kafkaSender, ObjectMapper objectMapper) {
        this.kafkaSender = kafkaSender;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> publish(AuditEvent event) {
        try {
            String value = objectMapper.writeValueAsString(event);
            var record = new ProducerRecord<>(TOPIC, event.entityId(), value);
            return kafkaSender.send(Mono.just(SenderRecord.create(record, event.id().toString())))
                    .doOnError(e -> log.error("Failed to publish audit event: {}", event.id(), e))
                    .then();
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
