package com.medfund.contributions.scheduler;

import com.medfund.contributions.service.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverdueContributionJob {
    private static final Logger log = LoggerFactory.getLogger(OverdueContributionJob.class);
    private final BillingService billingService;

    public OverdueContributionJob(BillingService billingService) {
        this.billingService = billingService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void execute() {
        log.info("Starting OverdueContributionJob");
        try {
            billingService.markOverdueContributions().block();
            log.info("OverdueContributionJob completed");
        } catch (Exception e) {
            log.error("OverdueContributionJob failed: {}", e.getMessage(), e);
        }
    }
}
