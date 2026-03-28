package com.medfund.contributions.controller;

import com.medfund.contributions.dto.ContributionResponse;
import com.medfund.contributions.dto.GenerateBillingRequest;
import com.medfund.contributions.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contributions")
@Tag(name = "Contributions", description = "Contribution billing, payment recording, and balance tracking")
@SecurityRequirement(name = "bearer-jwt")
public class ContributionController {

    private final BillingService billingService;

    public ContributionController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "List contributions by member")
    public Flux<ContributionResponse> findByMemberId(@PathVariable UUID memberId) {
        return billingService.findContributionsByMemberId(memberId).map(ContributionResponse::from);
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "List contributions by group")
    public Flux<ContributionResponse> findByGroupId(@PathVariable UUID groupId) {
        return billingService.findContributionsByGroupId(groupId).map(ContributionResponse::from);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List contributions by status")
    public Flux<ContributionResponse> findByStatus(@PathVariable String status) {
        return billingService.findContributionsByStatus(status).map(ContributionResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contribution by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contribution found"),
        @ApiResponse(responseCode = "404", description = "Contribution not found")
    })
    public Mono<ContributionResponse> findById(@PathVariable UUID id) {
        return billingService.findContributionById(id).map(ContributionResponse::from);
    }

    @PostMapping("/generate-billing")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate billing cycle",
        description = "Creates contribution records for members in a scheme/group for a billing period")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Billing cycle generated"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public Mono<Long> generateBilling(@Valid @RequestBody GenerateBillingRequest request, Principal principal) {
        return billingService.generateBilling(request, principal.getName());
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Record contribution payment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment recorded"),
        @ApiResponse(responseCode = "404", description = "Contribution not found")
    })
    public Mono<ContributionResponse> recordPayment(@PathVariable UUID id,
                                                    @RequestParam String paymentMethod,
                                                    @RequestParam(required = false) String paymentReference,
                                                    Principal principal) {
        return billingService.recordPayment(id, paymentMethod, paymentReference, principal.getName())
                .map(ContributionResponse::from);
    }
}
