package com.medfund.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateRoleRequest(
    @NotBlank @Size(max = 100)
    String name,

    @NotBlank @Size(max = 200)
    String displayName,

    String description,

    List<PermissionEntry> permissions
) {
    public record PermissionEntry(
        @NotBlank String permission,
        String accessLevel
    ) {
        public String accessLevelOrDefault() {
            return (accessLevel == null || accessLevel.isBlank()) ? "full" : accessLevel;
        }
    }
}
