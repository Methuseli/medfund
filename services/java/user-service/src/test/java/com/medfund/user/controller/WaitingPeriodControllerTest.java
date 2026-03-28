package com.medfund.user.controller;

import com.medfund.user.config.SecurityConfig;
import com.medfund.user.entity.WaitingPeriodRule;
import com.medfund.user.exception.GlobalExceptionHandler;
import com.medfund.user.service.WaitingPeriodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(WaitingPeriodController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class WaitingPeriodControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    WaitingPeriodService waitingPeriodService;

    @Test
    void findBySchemeId_returns200() {
        UUID schemeId = UUID.randomUUID();
        when(waitingPeriodService.findBySchemeId(schemeId)).thenReturn(Flux.just(createTestRule()));

        webTestClient.mutateWith(mockJwt())
                .get().uri("/api/v1/waiting-periods/scheme/{schemeId}", schemeId)
                .header("X-Tenant-ID", "test-tenant")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void checkEligibility_returns200() {
        UUID schemeId = UUID.randomUUID();
        when(waitingPeriodService.isWaitingPeriodSatisfied(any(UUID.class), anyString(), any()))
                .thenReturn(Mono.just(true));

        webTestClient.mutateWith(mockJwt())
                .get().uri(uriBuilder -> uriBuilder
                        .path("/api/v1/waiting-periods/check")
                        .queryParam("schemeId", schemeId)
                        .queryParam("conditionType", "maternity")
                        .queryParam("enrollmentDate", "2024-01-15")
                        .build())
                .header("X-Tenant-ID", "test-tenant")
                .exchange()
                .expectStatus().isOk();
    }

    private WaitingPeriodRule createTestRule() {
        var r = new WaitingPeriodRule();
        r.setId(UUID.randomUUID());
        r.setSchemeId(UUID.randomUUID());
        r.setConditionType("maternity");
        r.setWaitingDays(270);
        r.setDescription("Maternity waiting period");
        return r;
    }
}
