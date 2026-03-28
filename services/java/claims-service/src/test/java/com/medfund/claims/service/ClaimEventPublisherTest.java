package com.medfund.claims.service;

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
class ClaimEventPublisherTest {

    @Mock
    private KafkaSender<String, String> kafkaSender;

    @Captor
    private ArgumentCaptor<Mono<SenderRecord<String, String, String>>> senderRecordCaptor;

    private ClaimEventPublisher claimEventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        claimEventPublisher = new ClaimEventPublisher(kafkaSender, objectMapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishClaimSubmitted_sendsToCorrectTopic() {
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(claimEventPublisher.publishClaimSubmitted("clm-1", "CLM-123", "mbr-1"))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.claims.submitted");
                    assertThat(record.key()).isEqualTo("clm-1");
                    assertThat(record.value()).contains("CLAIM_SUBMITTED");
                    assertThat(record.value()).contains("CLM-123");
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishClaimAdjudicated_sendsToCorrectTopic() {
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(claimEventPublisher.publishClaimAdjudicated("clm-1", "CLM-123", "APPROVED"))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.claims.adjudicated");
                    assertThat(record.key()).isEqualTo("clm-1");
                    assertThat(record.value()).contains("CLAIM_ADJUDICATED");
                    assertThat(record.value()).contains("APPROVED");
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishClaimStatusChanged_sendsToCorrectTopic() {
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(claimEventPublisher.publishClaimStatusChanged("clm-1", "PAID"))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.claims.lifecycle");
                    assertThat(record.key()).isEqualTo("clm-1");
                    assertThat(record.value()).contains("CLAIM_STATUS_CHANGED");
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishPreAuthDecision_sendsToCorrectTopic() {
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.empty());

        StepVerifier.create(claimEventPublisher.publishPreAuthDecision("pa-1", "PA-123", "APPROVED"))
                .verifyComplete();

        verify(kafkaSender).send(senderRecordCaptor.capture());

        StepVerifier.create(senderRecordCaptor.getValue())
                .assertNext(record -> {
                    assertThat(record.topic()).isEqualTo("medfund.claims.pre-auth-decision");
                    assertThat(record.key()).isEqualTo("pa-1");
                    assertThat(record.value()).contains("PRE_AUTH_DECISION");
                })
                .verifyComplete();
    }
}
