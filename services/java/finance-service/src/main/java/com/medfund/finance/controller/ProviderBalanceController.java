package com.medfund.finance.controller;

import com.medfund.finance.dto.ProviderBalanceResponse;
import com.medfund.finance.service.ProviderBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/provider-balances")
@Tag(name = "Provider Balances", description = "Provider balance tracking — claimed, approved, paid, outstanding")
@SecurityRequirement(name = "bearer-jwt")
public class ProviderBalanceController {

    private final ProviderBalanceService providerBalanceService;

    public ProviderBalanceController(ProviderBalanceService providerBalanceService) {
        this.providerBalanceService = providerBalanceService;
    }

    @GetMapping
    @Operation(summary = "List all provider balances with outstanding amounts")
    public Flux<ProviderBalanceResponse> findAll() {
        return providerBalanceService.findAllByOutstandingBalance().map(ProviderBalanceResponse::from);
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "Get balance for a specific provider")
    public Mono<ProviderBalanceResponse> findByProviderId(@PathVariable UUID providerId) {
        return providerBalanceService.findByProviderId(providerId).map(ProviderBalanceResponse::from);
    }
}
