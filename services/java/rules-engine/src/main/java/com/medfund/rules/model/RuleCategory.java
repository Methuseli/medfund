package com.medfund.rules.model;

/**
 * Categories for classifying adjudication rules.
 * Each category corresponds to a stage in the claims adjudication pipeline.
 */
public enum RuleCategory {
    ELIGIBILITY,
    WAITING_PERIOD,
    BENEFIT_LIMIT,
    PRE_AUTHORIZATION,
    TARIFF_PRICING,
    CLINICAL_VALIDATION
}
