package com.medfund.claims.controller;

import com.medfund.claims.dto.ClaimResponse;
import com.medfund.claims.dto.SubmitDrugClaimRequest;
import com.medfund.claims.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/drug-claims")
@Tag(name = "Drug Claims", description = "Pharmaceutical/drug claim submission and management")
@SecurityRequirement(name = "bearer-jwt")
public class DrugClaimController {

    private final ClaimService claimService;

    public DrugClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    @Operation(summary = "List all drug claims")
    public Flux<ClaimResponse> findAll() {
        return claimService.findByClaimType("drug").map(ClaimResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a drug claim", description = "Submit pharmaceutical claim with prescription details")
    public Mono<ClaimResponse> submit(@Valid @RequestBody SubmitDrugClaimRequest request, Principal principal) {
        return claimService.submitDrugClaim(request, principal.getName()).map(ClaimResponse::from);
    }
}
