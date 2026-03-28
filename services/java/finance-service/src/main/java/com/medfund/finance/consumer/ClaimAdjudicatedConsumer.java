package com.medfund.finance.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.finance.service.ProviderBalanceService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Component
public class ClaimAdjudicatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClaimAdjudicatedConsumer.class);
    private static final String TOPIC = "medfund.claims.adjudicated";

    private final ReceiverOptions<String, String> receiverOptions;
    private final ProviderBalanceService providerBalanceService;
    private final ObjectMapper objectMapper;

    public ClaimAdjudicatedConsumer(ReceiverOptions<String, String> receiverOptions,
                                    ProviderBalanceService providerBalanceService,
                                    ObjectMapper objectMapper) {
        this.receiverOptions = receiverOptions;
        this.providerBalanceService = providerBalanceService;
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
                        .doOnError(e -> log.error("Failed to process claim adjudicated event: {}", e.getMessage()));
                } catch (Exception e) {
                    log.error("Error deserializing claim adjudicated event: {}", e.getMessage());
                    record.receiverOffset().acknowledge();
                    return Mono.empty();
                }
            })
            .doOnError(e -> log.error("Claim adjudicated consumer error: {}", e.getMessage()))
            .retry()
            .subscribe();
    }

    public Mono<Void> processEvent(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            String decision = node.get("decision").asText();

            if ("APPROVED".equals(decision) || "PARTIAL_APPROVED".equals(decision)) {
                String providerId = node.get("providerId").asText();
                String approvedAmount = node.get("approvedAmount").asText();
                String currencyCode = node.get("currencyCode").asText();
                log.info("Processing claim adjudicated event: decision={}, providerId={}, approvedAmount={}, currency={}",
                         decision, providerId, approvedAmount, currencyCode);
                return providerBalanceService.updateBalance(
                    UUID.fromString(providerId),
                    currencyCode,
                    null,
                    new BigDecimal(approvedAmount),
                    null,
                    "system"
                ).then();
            }

            log.info("Skipping claim adjudicated event with decision={}", decision);
            return Mono.empty();
        } catch (Exception e) {
            log.error("Failed to parse claim adjudicated event: {}", e.getMessage());
            return Mono.error(e);
        }
    }
}
