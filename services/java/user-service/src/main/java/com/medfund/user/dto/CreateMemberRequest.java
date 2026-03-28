package com.medfund.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateMemberRequest(
    @NotBlank @Size(max = 100)
    String firstName,

    @NotBlank @Size(max = 100)
    String lastName,

    @NotNull
    LocalDate dateOfBirth,

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

    UUID schemeId,

    LocalDate enrollmentDate
) {
    public LocalDate enrollmentDateOrDefault() {
        return enrollmentDate != null ? enrollmentDate : LocalDate.now();
    }
}
