package com.medfund.finance.controller;

import com.medfund.finance.config.SecurityConfig;
import com.medfund.finance.entity.ProviderBalance;
import com.medfund.finance.service.ProviderBalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(ProviderBalanceController.class)
@Import(SecurityConfig.class)
class ProviderBalanceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProviderBalanceService providerBalanceService;

    @Test
    void findAll_returns200() {
        when(providerBalanceService.findAllByOutstandingBalance()).thenReturn(Flux.just(createTestBalance()));

        webTestClient.mutateWith(mockJwt())
                .get().uri("/api/v1/provider-balances")
                .header("X-Tenant-ID", "test-tenant")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void findByProviderId_returns200() {
        UUID providerId = UUID.randomUUID();
        when(providerBalanceService.findByProviderId(providerId)).thenReturn(Mono.just(createTestBalance()));

        webTestClient.mutateWith(mockJwt())
                .get().uri("/api/v1/provider-balances/provider/{providerId}", providerId)
                .header("X-Tenant-ID", "test-tenant")
                .exchange()
                .expectStatus().isOk();
    }

    private ProviderBalance createTestBalance() {
        var b = new ProviderBalance();
        b.setId(UUID.randomUUID());
        b.setProviderId(UUID.randomUUID());
        b.setTotalClaimed(new BigDecimal("10000.00"));
        b.setTotalApproved(new BigDecimal("8000.00"));
        b.setTotalPaid(new BigDecimal("5000.00"));
        b.setOutstandingBalance(new BigDecimal("3000.00"));
        b.setCurrencyCode("USD");
        b.setLastUpdatedAt(Instant.now());
        b.setCreatedAt(Instant.now());
        return b;
    }
}
