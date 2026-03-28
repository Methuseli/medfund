package com.medfund.finance.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.finance.entity.ProviderBalance;
import com.medfund.finance.service.ProviderBalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimAdjudicatedConsumerTest {

    @Mock
    private ProviderBalanceService providerBalanceService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ClaimAdjudicatedConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ClaimAdjudicatedConsumer(null, providerBalanceService, objectMapper);
    }

    @Test
    void processEvent_approvedClaim_updatesProviderBalance() {
        String providerId = UUID.randomUUID().toString();
        String json = """
            {"event":"CLAIM_ADJUDICATED","decision":"APPROVED","providerId":"%s","approvedAmount":"1500.00","currencyCode":"USD"}
            """.formatted(providerId);
        when(providerBalanceService.updateBalance(any(), any(), any(), any(), any(), any()))
            .thenReturn(Mono.just(new ProviderBalance()));

        StepVerifier.create(consumer.processEvent(json))
            .verifyComplete();

        verify(providerBalanceService).updateBalance(
            UUID.fromString(providerId),
            "USD",
            null,
            new BigDecimal("1500.00"),
            null,
            "system"
        );
    }

    @Test
    void processEvent_rejectedClaim_skips() {
        String json = """
            {"event":"CLAIM_ADJUDICATED","decision":"REJECTED","providerId":"%s","approvedAmount":"0","currencyCode":"USD"}
            """.formatted(UUID.randomUUID().toString());

        StepVerifier.create(consumer.processEvent(json))
            .verifyComplete();

        verifyNoInteractions(providerBalanceService);
    }
}
