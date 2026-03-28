package com.medfund.user.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.user.service.RoleService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Component
public class TenantProvisionedConsumer {

    private static final Logger log = LoggerFactory.getLogger(TenantProvisionedConsumer.class);
    private static final String TOPIC = "medfund.tenants.provisioned";

    private final ReceiverOptions<String, String> receiverOptions;
    private final RoleService roleService;
    private final ObjectMapper objectMapper;

    public TenantProvisionedConsumer(ReceiverOptions<String, String> receiverOptions,
                                     RoleService roleService, ObjectMapper objectMapper) {
        this.receiverOptions = receiverOptions;
        this.roleService = roleService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void consume() {
        var options = receiverOptions.subscription(Collections.singleton(TOPIC));
        KafkaReceiver.create(options)
            .receive()
            .flatMap(record -> {
                try {
                    return processEvent(record.value())
                        .doOnSuccess(v -> record.receiverOffset().acknowledge())
                        .doOnError(e -> log.error("Failed to process tenant provisioned event: {}", e.getMessage()));
                } catch (Exception e) {
                    log.error("Error deserializing tenant provisioned event: {}", e.getMessage());
                    record.receiverOffset().acknowledge();
                    return Mono.empty();
                }
            })
            .doOnError(e -> log.error("Tenant provisioned consumer error: {}", e.getMessage()))
            .retry()
            .subscribe();
    }

    public Mono<Void> processEvent(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            String tenantId = node.get("tenantId").asText();
            String slug = node.get("slug").asText();
            log.info("Processing tenant provisioned event: tenantId={}, slug={}", tenantId, slug);
            return roleService.seedDefaultRoles(tenantId)
                .then();
        } catch (Exception e) {
            log.error("Failed to parse tenant provisioned event: {}", e.getMessage());
            return Mono.error(e);
        }
    }
}
