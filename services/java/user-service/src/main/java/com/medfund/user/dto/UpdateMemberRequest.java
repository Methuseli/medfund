package com.medfund.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateMemberRequest(
    @Size(max = 100)
    String firstName,

    @Size(max = 100)
    String lastName,

    @Size(max = 10)
    String gender,

    @Size(max = 50)
    String nationalId,

    @Email @Size(max = 255)
    String email,

    @Size(max = 50)
    String phone,

    String address,

    UUID groupId,

    UUID schemeId
) {}
