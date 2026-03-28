package com.medfund.finance.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.finance.service.PaymentRunService;
import com.medfund.shared.scheduler.JobExecutor;
import com.medfund.shared.scheduler.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PaymentRunExecutor implements JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(PaymentRunExecutor.class);
    private final PaymentRunService paymentRunService;
    private final ObjectMapper objectMapper;

    public PaymentRunExecutor(PaymentRunService paymentRunService, ObjectMapper objectMapper) {
        this.paymentRunService = paymentRunService;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType getJobType() { return JobType.PAYMENT_RUN; }

    @Override
    public Mono<Void> execute(String tenantId, String settings) {
        log.info("Executing payment run for tenant: {}", tenantId);
        try {
            JsonNode config = objectMapper.readTree(settings);
            int autoExecuteAfterHours = config.has("autoExecuteAfterHours") ? config.get("autoExecuteAfterHours").asInt() : 24;
            log.info("Auto-execute threshold: {} hours for tenant: {}", autoExecuteAfterHours, tenantId);
            return paymentRunService.autoExecuteDraftRuns();
        } catch (Exception e) {
            log.error("Failed to parse payment run settings: {}", e.getMessage());
            return paymentRunService.autoExecuteDraftRuns();
        }
    }
}
