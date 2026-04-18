package com.medfund.user.controller;

import com.medfund.user.dto.CreateStaffUserRequest;
import com.medfund.user.dto.StaffUserResponse;
import com.medfund.user.dto.UpdateStaffUserRequest;
import com.medfund.user.service.StaffUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff-users")
@Tag(name = "Staff Users", description = "Platform staff user management — creates users in DB and syncs to Keycloak")
@SecurityRequirement(name = "bearer-jwt")
public class StaffUserController {

    private final StaffUserService service;

    public StaffUserController(StaffUserService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all staff users")
    public Flux<StaffUserResponse> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String q) {

        if (q != null && !q.isBlank()) {
            return service.search(q).map(StaffUserResponse::from);
        }
        if (status != null) {
            return service.findByStatus(status).map(StaffUserResponse::from);
        }
        if (role != null) {
            return service.findByRole(role).map(StaffUserResponse::from);
        }
        return service.findAll().map(StaffUserResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get staff user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public Mono<StaffUserResponse> findById(
            @Parameter(description = "Staff user UUID") @PathVariable UUID id) {
        return service.findById(id).map(StaffUserResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a staff user",
               description = "Creates the user in PostgreSQL and syncs to the medfund-platform Keycloak realm. " +
                             "The user will receive an email to set their password.")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    public Mono<StaffUserResponse> create(
            @Valid @RequestBody CreateStaffUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String actorId = jwt != null ? jwt.getSubject() : null;
        return service.create(request, actorId).map(StaffUserResponse::from);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a staff user")
    public Mono<StaffUserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String actorId = jwt != null ? jwt.getSubject() : null;
        return service.update(id, request, actorId).map(StaffUserResponse::from);
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend a staff user", description = "Disables the account in both DB and Keycloak.")
    public Mono<StaffUserResponse> suspend(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        String actorId = jwt != null ? jwt.getSubject() : null;
        return service.suspend(id, actorId).map(StaffUserResponse::from);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate a suspended staff user", description = "Re-enables the account in both DB and Keycloak.")
    public Mono<StaffUserResponse> activate(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        String actorId = jwt != null ? jwt.getSubject() : null;
        return service.activate(id, actorId).map(StaffUserResponse::from);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a staff user", description = "Removes the user from DB and disables in Keycloak.")
    public Mono<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        String actorId = jwt != null ? jwt.getSubject() : null;
        return service.delete(id, actorId);
    }
}
