package com.medfund.user.controller;

import com.medfund.user.dto.CreateProviderRequest;
import com.medfund.user.dto.ProviderResponse;
import com.medfund.user.dto.UpdateProviderRequest;
import com.medfund.user.service.ProviderService;
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
@RequestMapping("/api/v1/providers")
@Tag(name = "Providers", description = "Provider onboarding, AHFOZ verification, and management")
@SecurityRequirement(name = "bearer-jwt")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping
    @Operation(summary = "List all providers")
    public Flux<ProviderResponse> findAll() {
        return providerService.findAll().map(ProviderResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get provider by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider found"),
        @ApiResponse(responseCode = "404", description = "Provider not found")
    })
    public Mono<ProviderResponse> findById(@PathVariable UUID id) {
        return providerService.findById(id).map(ProviderResponse::from);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List providers by status")
    public Flux<ProviderResponse> findByStatus(@PathVariable String status) {
        return providerService.findByStatus(status).map(ProviderResponse::from);
    }

    @GetMapping("/specialty/{specialty}")
    @Operation(summary = "List providers by specialty")
    public Flux<ProviderResponse> findBySpecialty(@PathVariable String specialty) {
        return providerService.findBySpecialty(specialty).map(ProviderResponse::from);
    }

    @GetMapping("/search")
    @Operation(summary = "Search providers by name or practice number")
    public Flux<ProviderResponse> search(@RequestParam String q) {
        return providerService.search(q).map(ProviderResponse::from);
    }

    @GetMapping("/ahfoz/{ahfozNumber}")
    @Operation(summary = "Find provider by AHFOZ number")
    public Mono<ProviderResponse> findByAhfozNumber(@PathVariable String ahfozNumber) {
        return providerService.findByAhfozNumber(ahfozNumber).map(ProviderResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Onboard a new provider",
        description = "Creates provider with pending_verification status, syncs to Keycloak, publishes onboarding event")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Provider onboarded"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public Mono<ProviderResponse> onboard(@Valid @RequestBody CreateProviderRequest request, Principal principal) {
        return providerService.onboard(request, principal.getName()).map(ProviderResponse::from);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update provider details")
    public Mono<ProviderResponse> update(@PathVariable UUID id,
                                          @Valid @RequestBody UpdateProviderRequest request,
                                          Principal principal) {
        return providerService.update(id, request, principal.getName()).map(ProviderResponse::from);
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify provider AHFOZ credentials", description = "Marks provider as active after AHFOZ verification")
    public Mono<ProviderResponse> verifyAhfoz(@PathVariable UUID id, Principal principal) {
        return providerService.verifyAhfoz(id, principal.getName()).map(ProviderResponse::from);
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend provider")
    public Mono<ProviderResponse> suspend(@PathVariable UUID id, Principal principal) {
        return providerService.suspend(id, principal.getName()).map(ProviderResponse::from);
    }
}
