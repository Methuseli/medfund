package com.medfund.shared.audit;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEvent(
        UUID id,
        String tenantId,
        String entityType,
        String entityId,
        String action,
        String actorId,
        String actorEmail,
        Map<String, Object> oldValue,
        Map<String, Object> newValue,
        String[] changedFields,
        String correlationId,
        Instant timestamp
) {
    public static AuditEvent create(
            String tenantId, String entityType, String entityId,
            String action, String actorId, String actorEmail,
            Map<String, Object> oldValue, Map<String, Object> newValue,
            String[] changedFields, String correlationId) {
        return new AuditEvent(
                UUID.randomUUID(), tenantId, entityType, entityId,
                action, actorId, actorEmail, oldValue, newValue,
                changedFields, correlationId, Instant.now()
        );
    }
}
