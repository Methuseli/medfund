package com.medfund.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeycloakSyncServiceTest {

    private KeycloakSyncService keycloakSyncService;

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(request -> Mono.error(new RuntimeException("connection refused")))
                .build();
        WebClient.Builder builder = mock(WebClient.Builder.class);
        when(builder.build()).thenReturn(webClient);
        keycloakSyncService = new KeycloakSyncService(builder);
    }

    @Test
    void createUser_onError_returnsEmpty() {
        StepVerifier.create(
                keycloakSyncService.createUser("test-realm", "user@test.com", "John", "Doe", List.of("member"))
        )
                .verifyComplete();
    }

    @Test
    void disableUser_onError_returnsEmpty() {
        StepVerifier.create(
                keycloakSyncService.disableUser("test-realm", "user-123")
        )
                .verifyComplete();
    }

    @Test
    void enableUser_onError_returnsEmpty() {
        StepVerifier.create(
                keycloakSyncService.enableUser("test-realm", "user-123")
        )
                .verifyComplete();
    }

    @Test
    void assignRealmRoles_onError_returnsEmpty() {
        StepVerifier.create(
                keycloakSyncService.assignRealmRoles("test-realm", "user-123", List.of("admin", "member"))
        )
                .verifyComplete();
    }

    @Test
    void removeRealmRole_onError_returnsEmpty() {
        StepVerifier.create(
                keycloakSyncService.removeRealmRole("test-realm", "user-123", "admin")
        )
                .verifyComplete();
    }
}
