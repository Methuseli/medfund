package com.medfund.contributions.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.contributions.service.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberEnrolledConsumerTest {

    @Mock
    private BillingService billingService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MemberEnrolledConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new MemberEnrolledConsumer(null, billingService, objectMapper);
    }

    @Test
    void processEvent_validPayload_createsInitialContribution() {
        String memberId = UUID.randomUUID().toString();
        String groupId = UUID.randomUUID().toString();
        String json = """
            {"event":"MEMBER_ENROLLED","memberId":"%s","memberNumber":"MEM-001","groupId":"%s"}
            """.formatted(memberId, groupId);
        when(billingService.createInitialContribution(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(consumer.processEvent(json))
            .verifyComplete();

        verify(billingService).createInitialContribution(
            UUID.fromString(memberId),
            UUID.fromString(groupId)
        );
    }

    @Test
    void processEvent_invalidJson_returnsError() {
        String json = "not valid json {{{";

        StepVerifier.create(consumer.processEvent(json))
            .verifyError();
    }
}
