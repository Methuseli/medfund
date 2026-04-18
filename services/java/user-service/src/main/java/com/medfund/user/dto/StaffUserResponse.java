package com.medfund.user.dto;

import com.medfund.user.entity.StaffUser;

import java.time.Instant;
import java.util.UUID;

public record StaffUserResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String jobTitle,
    String department,
    String realmRole,
    String keycloakUserId,
    String status,
    Instant createdAt,
    Instant updatedAt
) {
    public static StaffUserResponse from(StaffUser u) {
        return new StaffUserResponse(
            u.getId(),
            u.getFirstName(),
            u.getLastName(),
            u.getEmail(),
            u.getPhone(),
            u.getJobTitle(),
            u.getDepartment(),
            u.getRealmRole(),
            u.getKeycloakUserId(),
            u.getStatus(),
            u.getCreatedAt(),
            u.getUpdatedAt()
        );
    }
}
