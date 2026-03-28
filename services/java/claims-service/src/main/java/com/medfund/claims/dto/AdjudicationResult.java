package com.medfund.claims.dto;

import java.math.BigDecimal;
import java.util.List;

public record AdjudicationResult(
        String decision,
        BigDecimal approvedAmount,
        String rejectionCode,
        String rejectionNotes,
        List<StageResult> stageResults
) {
    public record StageResult(
            String stageName,
            boolean passed,
            String details
    ) {
    }
}
