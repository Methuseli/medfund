package com.medfund.user.controller;

import com.medfund.user.dto.WaitingPeriodRuleResponse;
import com.medfund.user.service.WaitingPeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/waiting-periods")
@Tag(name = "Waiting Periods", description = "Waiting period rule lookup and eligibility checking")
@SecurityRequirement(name = "bearer-jwt")
public class WaitingPeriodController {

    private final WaitingPeriodService waitingPeriodService;

    public WaitingPeriodController(WaitingPeriodService waitingPeriodService) {
        this.waitingPeriodService = waitingPeriodService;
    }

    @GetMapping("/scheme/{schemeId}")
    @Operation(summary = "List waiting period rules for a scheme")
    public Flux<WaitingPeriodRuleResponse> findBySchemeId(@PathVariable UUID schemeId) {
        return waitingPeriodService.findBySchemeId(schemeId).map(WaitingPeriodRuleResponse::from);
    }

    @GetMapping("/check")
    @Operation(summary = "Check if waiting period is satisfied",
        description = "Returns true if the member's enrollment date satisfies the waiting period for the given condition type")
    public Mono<Boolean> checkEligibility(
            @RequestParam UUID schemeId,
            @RequestParam String conditionType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enrollmentDate) {
        return waitingPeriodService.isWaitingPeriodSatisfied(schemeId, conditionType, enrollmentDate);
    }
}
