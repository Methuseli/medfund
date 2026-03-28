package com.medfund.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProviderRequest(
    @NotBlank @Size(max = 200)
    String name,

    @Size(max = 50)
    String practiceNumber,

    @Size(max = 50)
    String ahfozNumber,

    @Size(max = 100)
    String specialty,

    @Email @Size(max = 255)
    String email,

    @Size(max = 50)
    String phone,

    String address,

    String bankingDetails
) {}
