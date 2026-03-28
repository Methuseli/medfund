package com.medfund.user.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.user.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantProvisionedConsumerTest {

    @Mock
    private RoleService roleService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private TenantProvisionedConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new TenantProvisionedConsumer(null, roleService, objectMapper);
    }

    @Test
    void processEvent_validPayload_seedsDefaultRoles() {
        String json = """
            {"event":"TENANT_PROVISIONED","tenantId":"t-1","slug":"test"}
            """;
        when(roleService.seedDefaultRoles(any())).thenReturn(Mono.empty());

        StepVerifier.create(consumer.processEvent(json))
            .verifyComplete();

        verify(roleService).seedDefaultRoles("t-1");
    }

    @Test
    void processEvent_invalidJson_returnsError() {
        String json = "not valid json {{{";

        StepVerifier.create(consumer.processEvent(json))
            .verifyError();
    }
}
