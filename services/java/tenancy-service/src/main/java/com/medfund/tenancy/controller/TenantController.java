package com.medfund.tenancy.controller;

import com.medfund.tenancy.dto.CreateTenantRequest;
import com.medfund.tenancy.dto.TenantResponse;
import com.medfund.tenancy.dto.UpdateTenantRequest;
import com.medfund.tenancy.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenants", description = "Tenant lifecycle management — super admin only")
@SecurityRequirement(name = "bearer-jwt")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    @Operation(summary = "List all tenants", description = "Returns all tenants ordered by creation date descending. Super admin only.")
    @ApiResponse(responseCode = "200", description = "List of tenants")
    public Flux<TenantResponse> findAll() {
        return tenantService.findAll().map(TenantResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant found"),
            @ApiResponse(responseCode = "404", description = "Tenant not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public Mono<TenantResponse> findById(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id) {
        return tenantService.findById(id).map(TenantResponse::from);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get tenant by slug", description = "Lookup tenant by unique slug (e.g., 'zmmas')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant found"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    public Mono<TenantResponse> findBySlug(
            @Parameter(description = "Tenant slug", example = "zmmas") @PathVariable String slug) {
        return tenantService.findBySlug(slug).map(TenantResponse::from);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List tenants by status", description = "Filter tenants by status: active, suspended, archived")
    @ApiResponse(responseCode = "200", description = "Filtered list of tenants")
    public Flux<TenantResponse> findByStatus(
            @Parameter(description = "Tenant status", example = "active") @PathVariable String status) {
        return tenantService.findByStatus(status).map(TenantResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new tenant",
            description = "Provisions a new tenant: creates DB schema, runs Flyway migrations, creates Keycloak realm, " +
                    "seeds default data, and publishes TENANT_PROVISIONED Kafka event.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tenant created and provisioned"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Slug already exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public Mono<TenantResponse> create(
            @Valid @RequestBody CreateTenantRequest request,
            Principal principal) {
        String actorId = principal != null ? principal.getName() : "system";
        return tenantService.create(request, actorId).map(TenantResponse::from);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant", description = "Update tenant details. Only provided fields are updated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant updated"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    public Mono<TenantResponse> update(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantRequest request,
            Principal principal) {
        String actorId = principal != null ? principal.getName() : "system";
        return tenantService.update(id, request, actorId).map(TenantResponse::from);
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend tenant", description = "Suspends a tenant. Users can no longer log in. Data is preserved.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant suspended"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    public Mono<TenantResponse> suspend(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id,
            Principal principal) {
        String actorId = principal != null ? principal.getName() : "system";
        return tenantService.suspend(id, actorId).map(TenantResponse::from);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate tenant", description = "Re-activates a suspended tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant activated"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    public Mono<TenantResponse> activate(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id,
            Principal principal) {
        String actorId = principal != null ? principal.getName() : "system";
        return tenantService.activate(id, actorId).map(TenantResponse::from);
    }
}
