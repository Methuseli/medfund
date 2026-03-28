package com.medfund.shared.scheduler;

public enum JobType {
    BILLING_CYCLE("Billing Cycle", "Generate contribution bills for members"),
    OVERDUE_CHECK("Overdue Check", "Mark unpaid contributions as overdue"),
    PAYMENT_RUN("Payment Run", "Auto-execute approved payment runs"),
    AGE_PROCESSING("Age Processing", "Check dependant age limits and update status"),
    PRE_AUTH_EXPIRY("Pre-Auth Expiry", "Expire pre-authorizations past their expiry date"),
    TARIFF_ACTIVATION("Tariff Activation", "Activate/deactivate tariff schedules by effective date");

    private final String displayName;
    private final String description;

    JobType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
