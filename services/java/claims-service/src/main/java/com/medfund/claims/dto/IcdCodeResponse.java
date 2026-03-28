package com.medfund.claims.dto;

import com.medfund.claims.entity.IcdCode;

import java.util.UUID;

public record IcdCodeResponse(
        UUID id,
        String code,
        String description,
        String category,
        String chapter,
        Boolean isActive
) {
    public static IcdCodeResponse from(IcdCode icdCode) {
        return new IcdCodeResponse(
                icdCode.getId(),
                icdCode.getCode(),
                icdCode.getDescription(),
                icdCode.getCategory(),
                icdCode.getChapter(),
                icdCode.getIsActive()
        );
    }
}
