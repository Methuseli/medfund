package com.medfund.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateStaffUserRequest(
    @Size(max = 100)
    String firstName,

    @Size(max = 100)
    String lastName,

    @Size(max = 50)
    String phone,

    @Size(max = 100)
    String jobTitle,

    @Size(max = 100)
    String department,

    String realmRole
) {}
