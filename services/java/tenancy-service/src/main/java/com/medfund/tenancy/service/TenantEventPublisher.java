package com.medfund.tenancy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.tenancy.entity.Tenant;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;

@Service
public class TenantEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TenantEventPublisher.class);

    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    public TenantEventPublisher(KafkaSender<String, String> kafkaSender, ObjectMapper objectMapper) {
        this.kafkaSender = kafkaSender;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> publishTenantProvisioned(Tenant tenant) {
        return publishEvent("medfund.tenants.provisioned", tenant.getId().toString(), Map.of(
                "event", "TENANT_PROVISIONED",
                "tenantId", tenant.getId().toString(),
                "slug", tenant.getSlug(),
                "schemaName", tenant.getSchemaName(),
                "keycloakRealm", tenant.getKeycloakRealm()
        ));
    }

    public Mono<Void> publishTenantSuspended(Tenant tenant) {
        return publishEvent("medfund.tenants.lifecycle", tenant.getId().toString(), Map.of(
                "event", "TENANT_SUSPENDED",
                "tenantId", tenant.getId().toString(),
                "slug", tenant.getSlug()
        ));
    }

    private Mono<Void> publishEvent(String topic, String key, Map<String, String> payload) {
        try {
            String value = objectMapper.writeValueAsString(payload);
            var record = new ProducerRecord<>(topic, key, value);
            return kafkaSender.send(Mono.just(SenderRecord.create(record, key)))
                    .doOnError(e -> log.error("Failed to publish event to {}: {}", topic, e.getMessage()))
                    .then();
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
