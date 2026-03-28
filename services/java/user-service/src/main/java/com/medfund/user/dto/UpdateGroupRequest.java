package com.medfund.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateGroupRequest(
    @Size(max = 200)
    String name,

    @Size(max = 100)
    String registrationNumber,

    @Size(max = 200)
    String contactPerson,

    @Email @Size(max = 255)
    String contactEmail,

    @Size(max = 50)
    String contactPhone,

    String address
) {}
