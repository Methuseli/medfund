package com.medfund.tenancy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.tenancy.entity.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantEventPublisherTest {

    @Mock
    private KafkaSender<String, String> kafkaSender;

    @Captor
    private ArgumentCaptor<Mono<SenderRecord<String, String, String>>> senderRecordCaptor;

    private TenantEventPublisher tenantEventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        tenantEventPublisher = new TenantEventPublisher(kafkaSender, objectMapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishTenantProvisioned_sendsToCorrectTopic() {
        Tenant tenant = createTestTenant();

        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(tenantEventPublisher.publishTenantProvisioned(tenant))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        // Subscribe to the captured Mono to extract the SenderRecord
        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.tenants.provisioned");
                    assertThat(record.key()).isEqualTo(tenant.getId().toString());
                    assertThat(record.value()).contains("TENANT_PROVISIONED");
                    assertThat(record.value()).contains(tenant.getSlug());
                    assertThat(record.value()).contains(tenant.getSchemaName());
                    assertThat(record.value()).contains(tenant.getKeycloakRealm());
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishTenantSuspended_sendsToCorrectTopic() {
        Tenant tenant = createTestTenant();

        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(tenantEventPublisher.publishTenantSuspended(tenant))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.tenants.lifecycle");
                    assertThat(record.key()).isEqualTo(tenant.getId().toString());
                    assertThat(record.value()).contains("TENANT_SUSPENDED");
                    assertThat(record.value()).contains(tenant.getSlug());
                })
                .verifyComplete();
    }

    private Tenant createTestTenant() {
        var tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setName("Test Society");
        tenant.setSlug("test-soc");
        tenant.setSchemaName("tenant_abc123");
        tenant.setStatus("active");
        tenant.setContactEmail("admin@test.com");
        tenant.setCountryCode("US");
        tenant.setTimezone("UTC");
        tenant.setMembershipModel("BOTH");
        tenant.setKeycloakRealm("medfund-test-soc");
        tenant.setCreatedAt(Instant.now());
        tenant.setUpdatedAt(Instant.now());
        return tenant;
    }
}
