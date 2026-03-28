package com.medfund.claims.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.claims.entity.Claim;
import com.medfund.claims.entity.ClaimLine;
import com.medfund.claims.entity.PreAuthorization;
import com.medfund.claims.entity.TariffCode;
import com.medfund.claims.repository.DiagnosisProcedureMappingRepository;
import com.medfund.claims.repository.IcdCodeRepository;
import com.medfund.claims.repository.PreAuthorizationRepository;
import com.medfund.claims.repository.RejectionReasonRepository;
import com.medfund.claims.repository.TariffCodeRepository;
import com.medfund.claims.repository.TariffModifierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdjudicationPipelineTest {

    @Mock
    private TariffCodeRepository tariffCodeRepository;

    @Mock
    private TariffModifierRepository tariffModifierRepository;

    @Mock
    private IcdCodeRepository icdCodeRepository;

    @Mock
    private DiagnosisProcedureMappingRepository diagnosisProcedureMappingRepository;

    @Mock
    private PreAuthorizationRepository preAuthorizationRepository;

    @Mock
    private RejectionReasonRepository rejectionReasonRepository;

    private AdjudicationPipeline adjudicationPipeline;

    @BeforeEach
    void setUp() {
        adjudicationPipeline = new AdjudicationPipeline(
                tariffCodeRepository,
                tariffModifierRepository,
                icdCodeRepository,
                diagnosisProcedureMappingRepository,
                preAuthorizationRepository,
                rejectionReasonRepository,
                new ObjectMapper()
        );
    }

    @Test
    void execute_allStagesPass_returnsApproved() {
        Claim claim = createTestClaim();
        ClaimLine line = createTestClaimLine(claim.getId(), "TC001");

        TariffCode tariffCode = createTestTariffCode("TC001", false);

        // Stage 4 (PreAuth): tariff found, no pre-auth required
        when(tariffCodeRepository.findByCode("TC001")).thenReturn(Mono.just(tariffCode));

        StepVerifier.create(adjudicationPipeline.execute(claim, List.of(line)))
                .assertNext(result -> {
                    assertThat(result.decision()).isEqualTo("APPROVED");
                    assertThat(result.approvedAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
                    assertThat(result.rejectionCode()).isNull();
                    assertThat(result.rejectionNotes()).isNull();
                    assertThat(result.stageResults()).hasSize(6);
                    assertThat(result.stageResults()).allMatch(
                            stage -> stage.passed()
                    );
                })
                .verifyComplete();
    }

    @Test
    void execute_preAuthRequired_noPreAuth_returnsRejected() {
        Claim claim = createTestClaim();
        ClaimLine line = createTestClaimLine(claim.getId(), "TC002");

        TariffCode tariffCode = createTestTariffCode("TC002", true);

        // Stage 4: tariff requires pre-auth
        when(tariffCodeRepository.findByCode("TC002")).thenReturn(Mono.just(tariffCode));
        // No approved pre-auth found
        when(preAuthorizationRepository.findByMemberIdAndTariffCodeAndStatus(
                eq(claim.getMemberId()), eq("TC002"), eq("APPROVED")))
                .thenReturn(Mono.empty());

        StepVerifier.create(adjudicationPipeline.execute(claim, List.of(line)))
                .assertNext(result -> {
                    assertThat(result.decision()).isEqualTo("REJECTED");
                    assertThat(result.approvedAmount()).isNull();
                    // The first failed stage should be PreAuthorization
                    assertThat(result.stageResults()).anyMatch(
                            stage -> "PreAuthorization".equals(stage.stageName()) && !stage.passed()
                    );
                })
                .verifyComplete();
    }

    @Test
    void execute_invalidTariffCode_returnsRejected() {
        Claim claim = createTestClaim();
        ClaimLine line = createTestClaimLine(claim.getId(), "INVALID_CODE");

        // Tariff code not found for both pre-auth check and pricing validation
        when(tariffCodeRepository.findByCode("INVALID_CODE")).thenReturn(Mono.empty());

        StepVerifier.create(adjudicationPipeline.execute(claim, List.of(line)))
                .assertNext(result -> {
                    assertThat(result.decision()).isEqualTo("REJECTED");
                    assertThat(result.approvedAmount()).isNull();
                    // TariffPricing stage should fail
                    assertThat(result.stageResults()).anyMatch(
                            stage -> "TariffPricing".equals(stage.stageName()) && !stage.passed()
                    );
                })
                .verifyComplete();
    }

    // ---- Helpers ----

    private Claim createTestClaim() {
        var claim = new Claim();
        claim.setId(UUID.randomUUID());
        claim.setClaimNumber("CLM-123456");
        claim.setMemberId(UUID.randomUUID());
        claim.setProviderId(UUID.randomUUID());
        claim.setSchemeId(UUID.randomUUID());
        claim.setClaimType("medical");
        claim.setStatus("VERIFIED");
        claim.setServiceDate(LocalDate.now());
        claim.setClaimedAmount(new BigDecimal("500.00"));
        claim.setCurrencyCode("USD");
        claim.setCreatedAt(Instant.now());
        claim.setUpdatedAt(Instant.now());
        claim.setCreatedBy(UUID.randomUUID());
        claim.setUpdatedBy(UUID.randomUUID());
        // No diagnosis codes — clinical validation will pass with "No diagnosis codes to validate"
        claim.setDiagnosisCodes(null);
        return claim;
    }

    private ClaimLine createTestClaimLine(UUID claimId, String tariffCode) {
        var line = new ClaimLine();
        line.setId(UUID.randomUUID());
        line.setClaimId(claimId);
        line.setTariffCode(tariffCode);
        line.setDescription("Test procedure");
        line.setQuantity(1);
        line.setUnitPrice(new BigDecimal("500.00"));
        line.setClaimedAmount(new BigDecimal("500.00"));
        line.setCurrencyCode("USD");
        line.setCreatedAt(Instant.now());
        return line;
    }

    private TariffCode createTestTariffCode(String code, boolean requiresPreAuth) {
        var tariffCode = new TariffCode();
        tariffCode.setId(UUID.randomUUID());
        tariffCode.setScheduleId(UUID.randomUUID());
        tariffCode.setCode(code);
        tariffCode.setDescription("Test tariff");
        tariffCode.setCategory("GENERAL");
        tariffCode.setUnitPrice(new BigDecimal("500.00"));
        tariffCode.setCurrencyCode("USD");
        tariffCode.setRequiresPreAuth(requiresPreAuth);
        tariffCode.setCreatedAt(Instant.now());
        return tariffCode;
    }
}
