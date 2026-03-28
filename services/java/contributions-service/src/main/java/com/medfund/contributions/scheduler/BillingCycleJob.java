package com.medfund.contributions.scheduler;

import com.medfund.contributions.service.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BillingCycleJob {
    private static final Logger log = LoggerFactory.getLogger(BillingCycleJob.class);
    private final BillingService billingService;

    public BillingCycleJob(BillingService billingService) {
        this.billingService = billingService;
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void execute() {
        log.info("Starting BillingCycleJob — Auto billing cycle triggered");
        try {
            billingService.runAutoBilling().block();
            log.info("BillingCycleJob completed");
        } catch (Exception e) {
            log.error("BillingCycleJob failed: {}", e.getMessage(), e);
        }
    }
}
