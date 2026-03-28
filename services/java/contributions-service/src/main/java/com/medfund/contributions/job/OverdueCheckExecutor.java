package com.medfund.contributions.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.contributions.service.BillingService;
import com.medfund.shared.scheduler.JobExecutor;
import com.medfund.shared.scheduler.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OverdueCheckExecutor implements JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(OverdueCheckExecutor.class);
    private final BillingService billingService;
    private final ObjectMapper objectMapper;

    public OverdueCheckExecutor(BillingService billingService, ObjectMapper objectMapper) {
        this.billingService = billingService;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType getJobType() { return JobType.OVERDUE_CHECK; }

    @Override
    public Mono<Void> execute(String tenantId, String settings) {
        log.info("Executing overdue check for tenant: {}", tenantId);
        try {
            JsonNode config = objectMapper.readTree(settings);
            int gracePeriodDays = config.has("gracePeriodDays") ? config.get("gracePeriodDays").asInt() : 30;
            log.info("Grace period: {} days for tenant: {}", gracePeriodDays, tenantId);
            // Pass grace period to the service — for now uses default logic
            return billingService.markOverdueContributions();
        } catch (Exception e) {
            log.error("Failed to parse overdue check settings: {}", e.getMessage());
            return billingService.markOverdueContributions();
        }
    }
}
