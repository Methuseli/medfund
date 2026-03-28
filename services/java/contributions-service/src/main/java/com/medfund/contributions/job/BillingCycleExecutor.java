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
public class BillingCycleExecutor implements JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(BillingCycleExecutor.class);
    private final BillingService billingService;
    private final ObjectMapper objectMapper;

    public BillingCycleExecutor(BillingService billingService, ObjectMapper objectMapper) {
        this.billingService = billingService;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType getJobType() { return JobType.BILLING_CYCLE; }

    @Override
    public Mono<Void> execute(String tenantId, String settings) {
        log.info("Executing billing cycle for tenant: {}", tenantId);
        try {
            JsonNode config = objectMapper.readTree(settings);
            int billingDayOfMonth = config.has("billingDayOfMonth") ? config.get("billingDayOfMonth").asInt() : 15;
            log.info("Billing day of month: {} for tenant: {}", billingDayOfMonth, tenantId);
            return billingService.runAutoBilling();
        } catch (Exception e) {
            log.error("Failed to parse billing cycle settings for tenant {}: {}", tenantId, e.getMessage());
            return Mono.empty();
        }
    }
}
