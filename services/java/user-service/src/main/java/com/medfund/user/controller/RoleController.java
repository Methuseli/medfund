package com.medfund.user.controller;

import com.medfund.user.dto.AssignRoleRequest;
import com.medfund.user.dto.CreateRoleRequest;
import com.medfund.user.dto.RoleResponse;
import com.medfund.user.entity.Role;
import com.medfund.user.entity.UserRole;
import com.medfund.user.service.RoleService;
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
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles & Permissions", description = "Role/permission management and user role assignment")
@SecurityRequirement(name = "bearer-jwt")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(summary = "List all roles")
    public Flux<Role> findAll() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID with permissions")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public Mono<RoleResponse> findById(@PathVariable UUID id) {
        return roleService.findByIdWithPermissions(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new role with permissions")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Role created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    public Mono<Role> create(@Valid @RequestBody CreateRoleRequest request, Principal principal) {
        return roleService.create(request, principal.getName());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List roles assigned to a user")
    public Flux<UserRole> findUserRoles(@PathVariable UUID userId) {
        return roleService.findUserRoles(userId);
    }

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Assign a role to a user", description = "Assigns role and syncs with Keycloak realm roles")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Role assigned"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public Mono<UserRole> assignRole(@Valid @RequestBody AssignRoleRequest request, Principal principal) {
        return roleService.assignRole(request, principal.getName());
    }

    @DeleteMapping("/user/{userId}/role/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke a role from a user")
    public Mono<Void> revokeRole(@PathVariable UUID userId, @PathVariable UUID roleId, Principal principal) {
        return roleService.revokeRole(userId, roleId, principal.getName());
    }
}
