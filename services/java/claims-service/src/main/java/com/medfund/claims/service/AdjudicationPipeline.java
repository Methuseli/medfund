package com.medfund.claims.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.claims.dto.AdjudicationResult;
import com.medfund.claims.dto.AdjudicationResult.StageResult;
import com.medfund.claims.entity.Claim;
import com.medfund.claims.entity.ClaimLine;
import com.medfund.claims.entity.DiagnosisProcedureMapping;
import com.medfund.claims.entity.TariffCode;
import com.medfund.claims.repository.DiagnosisProcedureMappingRepository;
import com.medfund.claims.repository.IcdCodeRepository;
import com.medfund.claims.repository.PreAuthorizationRepository;
import com.medfund.claims.repository.RejectionReasonRepository;
import com.medfund.claims.repository.TariffCodeRepository;
import com.medfund.claims.repository.TariffModifierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Six-stage adjudication pipeline for claims processing.
 * <p>
 * Stages:
 * 1. Eligibility — verifies member, provider, and scheme are present
 * 2. Waiting Period — stub, delegated to rules engine
 * 3. Benefit Limits — stub, delegated to rules engine
 * 4. Pre-Authorization — checks if required pre-auths exist and are valid
 * 5. Tariff Pricing — validates tariff codes and price limits
 * 6. Clinical Validation — checks diagnosis-procedure mappings
 * <p>
 * Full rules engine integration will replace the stub stages.
 */
@Service
public class AdjudicationPipeline {

    private static final Logger log = LoggerFactory.getLogger(AdjudicationPipeline.class);

    private final TariffCodeRepository tariffCodeRepository;
    private final TariffModifierRepository tariffModifierRepository;
    private final IcdCodeRepository icdCodeRepository;
    private final DiagnosisProcedureMappingRepository diagnosisProcedureMappingRepository;
    private final PreAuthorizationRepository preAuthorizationRepository;
    private final RejectionReasonRepository rejectionReasonRepository;
    private final ObjectMapper objectMapper;

    public AdjudicationPipeline(TariffCodeRepository tariffCodeRepository,
                                TariffModifierRepository tariffModifierRepository,
                                IcdCodeRepository icdCodeRepository,
                                DiagnosisProcedureMappingRepository diagnosisProcedureMappingRepository,
                                PreAuthorizationRepository preAuthorizationRepository,
                                RejectionReasonRepository rejectionReasonRepository,
                                ObjectMapper objectMapper) {
        this.tariffCodeRepository = tariffCodeRepository;
        this.tariffModifierRepository = tariffModifierRepository;
        this.icdCodeRepository = icdCodeRepository;
        this.diagnosisProcedureMappingRepository = diagnosisProcedureMappingRepository;
        this.preAuthorizationRepository = preAuthorizationRepository;
        this.rejectionReasonRepository = rejectionReasonRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Executes the 6-stage adjudication pipeline sequentially.
     *
     * @param claim the claim being adjudicated
     * @param lines the claim lines to validate
     * @return the adjudication decision
     */
    public Mono<AdjudicationResult> execute(Claim claim, List<ClaimLine> lines) {
        return checkEligibility(claim)
            .flatMap(s1 -> checkWaitingPeriod(claim).map(s2 -> {
                var results = new ArrayList<StageResult>();
                results.add(s1);
                results.add(s2);
                return results;
            }))
            .flatMap(results -> checkBenefitLimits(claim).map(s3 -> {
                results.add(s3);
                return results;
            }))
            .flatMap(results -> checkPreAuth(claim, lines).map(s4 -> {
                results.add(s4);
                return results;
            }))
            .flatMap(results -> validateTariffPricing(claim, lines).map(s5 -> {
                results.add(s5);
                return results;
            }))
            .flatMap(results -> validateClinical(claim, lines).map(s6 -> {
                results.add(s6);
                return results;
            }))
            .map(results -> buildDecision(claim, lines, results));
    }

    // ---- Stage 1: Eligibility ----

    private Mono<StageResult> checkEligibility(Claim claim) {
        boolean hasMember = claim.getMemberId() != null;
        boolean hasProvider = claim.getProviderId() != null;
        boolean hasScheme = claim.getSchemeId() != null;
        boolean passed = hasMember && hasProvider && hasScheme;

        String details;
        if (passed) {
            details = "Eligibility check passed: member, provider, and scheme are present";
        } else {
            var missing = new ArrayList<String>();
            if (!hasMember) missing.add("memberId");
            if (!hasProvider) missing.add("providerId");
            if (!hasScheme) missing.add("schemeId");
            details = "Eligibility check failed: missing " + String.join(", ", missing);
        }

        return Mono.just(new StageResult("Eligibility", passed, details));
    }

    // ---- Stage 2: Waiting Period (stub) ----

    private Mono<StageResult> checkWaitingPeriod(Claim claim) {
        return Mono.just(new StageResult("WaitingPeriod", true,
            "Waiting period check delegated to rules engine"));
    }

    // ---- Stage 3: Benefit Limits (stub) ----

    private Mono<StageResult> checkBenefitLimits(Claim claim) {
        return Mono.just(new StageResult("BenefitLimits", true,
            "Benefit limit check delegated to rules engine"));
    }

    // ---- Stage 4: Pre-Authorization ----

    private Mono<StageResult> checkPreAuth(Claim claim, List<ClaimLine> lines) {
        return Flux.fromIterable(lines)
            .flatMap(line -> tariffCodeRepository.findByCode(line.getTariffCode())
                .flatMap(tariff -> {
                    if (Boolean.TRUE.equals(tariff.getRequiresPreAuth())) {
                        return preAuthorizationRepository
                            .findByMemberIdAndTariffCodeAndStatus(claim.getMemberId(), tariff.getCode(), "APPROVED")
                            .map(preAuth -> {
                                boolean valid = preAuth.getExpiryDate() != null
                                    && !preAuth.getExpiryDate().isBefore(LocalDate.now());
                                return valid
                                    ? "Pre-auth valid for " + tariff.getCode()
                                    : "Pre-auth expired for " + tariff.getCode();
                            })
                            .defaultIfEmpty("Pre-auth required but not found for " + tariff.getCode());
                    }
                    return Mono.just("No pre-auth required for " + tariff.getCode());
                })
                .defaultIfEmpty("Tariff code " + line.getTariffCode() + " not found — skipping pre-auth check"))
            .collectList()
            .map(details -> {
                boolean passed = details.stream().noneMatch(d ->
                    d.contains("required but not found") || d.contains("expired"));
                return new StageResult("PreAuthorization", passed, String.join("; ", details));
            });
    }

    // ---- Stage 5: Tariff Pricing Validation ----

    private Mono<StageResult> validateTariffPricing(Claim claim, List<ClaimLine> lines) {
        return Flux.fromIterable(lines)
            .flatMap(line -> tariffCodeRepository.findByCode(line.getTariffCode())
                .map(tariff -> validateLinePrice(line, tariff))
                .defaultIfEmpty("Tariff code not found: " + line.getTariffCode() + " — FAIL"))
            .collectList()
            .map(details -> {
                boolean passed = details.stream().noneMatch(d -> d.contains("FAIL"));
                return new StageResult("TariffPricing", passed, String.join("; ", details));
            });
    }

    private String validateLinePrice(ClaimLine line, TariffCode tariff) {
        BigDecimal maxAllowed = tariff.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
        if (line.getClaimedAmount().compareTo(maxAllowed) > 0) {
            BigDecimal excess = line.getClaimedAmount().subtract(maxAllowed);
            return "Line " + line.getTariffCode() + " claimed " + line.getClaimedAmount()
                + " exceeds tariff limit " + maxAllowed + " by " + excess + " — FAIL";
        }
        return "Line " + line.getTariffCode() + " within tariff limit (" + line.getClaimedAmount()
            + " <= " + maxAllowed + ")";
    }

    // ---- Stage 6: Clinical Validation ----

    private Mono<StageResult> validateClinical(Claim claim, List<ClaimLine> lines) {
        List<String> diagnosisCodes = parseDiagnosisCodes(claim.getDiagnosisCodes());
        if (diagnosisCodes.isEmpty()) {
            return Mono.just(new StageResult("ClinicalValidation", true,
                "No diagnosis codes to validate"));
        }

        return Flux.fromIterable(diagnosisCodes)
            .flatMap(diagCode -> Flux.fromIterable(lines)
                .flatMap(line -> diagnosisProcedureMappingRepository
                    .findByIcdCodeAndTariffCode(diagCode, line.getTariffCode())
                    .map(mapping -> validateMapping(diagCode, line.getTariffCode(), mapping))
                    .defaultIfEmpty("No mapping found for " + diagCode + " + " + line.getTariffCode() + " — allowed")))
            .collectList()
            .map(details -> {
                boolean hasInvalid = details.stream().anyMatch(d -> d.contains("INVALID"));
                return new StageResult("ClinicalValidation", !hasInvalid, String.join("; ", details));
            });
    }

    private String validateMapping(String diagCode, String tariffCode, DiagnosisProcedureMapping mapping) {
        if ("INVALID".equalsIgnoreCase(mapping.getValidity())) {
            return "Diagnosis " + diagCode + " + procedure " + tariffCode + " is INVALID: "
                + (mapping.getNotes() != null ? mapping.getNotes() : "no details");
        }
        return "Diagnosis " + diagCode + " + procedure " + tariffCode + " is " + mapping.getValidity();
    }

    private List<String> parseDiagnosisCodes(String diagnosisCodesJson) {
        if (diagnosisCodesJson == null || diagnosisCodesJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(diagnosisCodesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse diagnosis codes JSON: {}", diagnosisCodesJson, e);
            return List.of();
        }
    }

    // ---- Decision Builder ----

    private AdjudicationResult buildDecision(Claim claim, List<ClaimLine> lines, List<StageResult> results) {
        boolean allPassed = results.stream().allMatch(StageResult::passed);
        boolean anyFailed = results.stream().anyMatch(r -> !r.passed());

        if (allPassed) {
            // All stages passed — approve for full claimed amount
            return new AdjudicationResult(
                "APPROVED",
                claim.getClaimedAmount(),
                null,
                null,
                results
            );
        }

        // Check if any stage has warning-like details (partial info)
        boolean hasWarnings = results.stream().anyMatch(r ->
            r.passed() && r.details() != null && r.details().contains("warning"));

        if (anyFailed) {
            // Find first failure for rejection details
            StageResult firstFailure = results.stream()
                .filter(r -> !r.passed())
                .findFirst()
                .orElse(results.get(0));

            // If failures are only in non-critical stages, flag for manual review
            boolean onlySoftFailures = results.stream()
                .filter(r -> !r.passed())
                .allMatch(r -> "WaitingPeriod".equals(r.stageName()) || "BenefitLimits".equals(r.stageName()));

            if (onlySoftFailures) {
                return new AdjudicationResult(
                    "MANUAL_REVIEW",
                    null,
                    firstFailure.stageName(),
                    firstFailure.details(),
                    results
                );
            }

            return new AdjudicationResult(
                "REJECTED",
                null,
                firstFailure.stageName(),
                firstFailure.details(),
                results
            );
        }

        // Fallback — manual review
        return new AdjudicationResult(
            "MANUAL_REVIEW",
            null,
            null,
            "Unable to determine automatic decision — flagged for manual review",
            results
        );
    }
}
