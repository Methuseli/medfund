package com.medfund.contributions.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InsuranceQuoteRequest(
    @NotNull UUID schemeId,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull LocalDate dateOfBirth,
    String email,
    String phone,
    String membershipModel,  // GROUP or INDIVIDUAL
    UUID groupId,            // if group membership
    List<DependantQuoteInfo> dependants
) {
    public record DependantQuoteInfo(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull LocalDate dateOfBirth,
        @NotBlank String relationship  // spouse, child, parent
    ) {}
}
