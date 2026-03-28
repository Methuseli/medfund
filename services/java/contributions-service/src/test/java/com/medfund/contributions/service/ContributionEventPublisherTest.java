package com.medfund.contributions.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContributionEventPublisherTest {

    @Mock
    private KafkaSender<String, String> kafkaSender;

    @Captor
    private ArgumentCaptor<Mono<SenderRecord<String, String, String>>> senderRecordCaptor;

    private ContributionEventPublisher contributionEventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        contributionEventPublisher = new ContributionEventPublisher(kafkaSender, objectMapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishBillingGenerated_sendsToCorrectTopic() {
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(contributionEventPublisher.publishBillingGenerated("scheme-1", "2026-01-01", "2026-01-31", 50))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.contributions.billing-generated");
                    assertThat(record.key()).isEqualTo("scheme-1");
                    assertThat(record.value()).contains("BILLING_GENERATED");
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishContributionPaid_sendsToCorrectTopic() {
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(contributionEventPublisher.publishContributionPaid("cont-1", "mbr-1", "150.00"))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.contributions.paid");
                    assertThat(record.key()).isEqualTo("cont-1");
                    assertThat(record.value()).contains("CONTRIBUTION_PAID");
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishInvoiceIssued_sendsToCorrectTopic() {
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(contributionEventPublisher.publishInvoiceIssued("inv-1", "INV-123", "grp-1"))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.contributions.invoice-issued");
                    assertThat(record.key()).isEqualTo("inv-1");
                    assertThat(record.value()).contains("INVOICE_ISSUED");
                })
                .verifyComplete();
    }
}
