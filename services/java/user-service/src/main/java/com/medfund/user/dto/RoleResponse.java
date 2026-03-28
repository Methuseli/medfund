package com.medfund.user.dto;

import com.medfund.user.entity.Role;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RoleResponse(
    UUID id,
    String name,
    String displayName,
    String description,
    Boolean isSystem,
    List<PermissionResponse> permissions,
    Instant createdAt,
    Instant updatedAt
) {
    public record PermissionResponse(
        UUID id,
        String permission,
        String accessLevel
    ) {}

    public static RoleResponse from(Role role, List<PermissionResponse> permissions) {
        return new RoleResponse(
            role.getId(), role.getName(), role.getDisplayName(),
            role.getDescription(), role.getIsSystem(),
            permissions, role.getCreatedAt(), role.getUpdatedAt()
        );
    }
}
