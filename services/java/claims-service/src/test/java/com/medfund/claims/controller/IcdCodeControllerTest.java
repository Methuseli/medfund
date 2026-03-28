package com.medfund.claims.controller;

import com.medfund.claims.config.SecurityConfig;
import com.medfund.claims.entity.IcdCode;
import com.medfund.claims.repository.DiagnosisProcedureMappingRepository;
import com.medfund.claims.repository.IcdCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(IcdCodeController.class)
@Import(SecurityConfig.class)
class IcdCodeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IcdCodeRepository icdCodeRepository;

    @MockBean
    private DiagnosisProcedureMappingRepository diagnosisProcedureMappingRepository;

    @Test
    void search_returns200() {
        when(icdCodeRepository.searchByCodeOrDescription("test")).thenReturn(Flux.just(createTestIcdCode()));

        webTestClient.mutateWith(mockJwt())
                .get().uri("/api/v1/icd-codes/search?q=test")
                .header("X-Tenant-ID", "test-tenant")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void findByCode_returns200() {
        when(icdCodeRepository.findByCode("A00.0")).thenReturn(Mono.just(createTestIcdCode()));

        webTestClient.mutateWith(mockJwt())
                .get().uri("/api/v1/icd-codes/{code}", "A00.0")
                .header("X-Tenant-ID", "test-tenant")
                .exchange()
                .expectStatus().isOk();
    }

    private IcdCode createTestIcdCode() {
        var code = new IcdCode();
        code.setId(UUID.randomUUID());
        code.setCode("A00.0");
        code.setDescription("Cholera due to Vibrio cholerae");
        code.setCategory("A00-A09");
        code.setChapter("I");
        code.setIsActive(true);
        return code;
    }
}
