package com.medfund.finance.scheduler;

import com.medfund.finance.service.PaymentRunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentRunAutoExecutionJob {
    private static final Logger log = LoggerFactory.getLogger(PaymentRunAutoExecutionJob.class);
    private final PaymentRunService paymentRunService;

    public PaymentRunAutoExecutionJob(PaymentRunService paymentRunService) {
        this.paymentRunService = paymentRunService;
    }

    @Scheduled(cron = "0 0 6 * * MON")
    public void execute() {
        log.info("Starting PaymentRunAutoExecutionJob");
        try {
            paymentRunService.autoExecuteDraftRuns().block();
            log.info("PaymentRunAutoExecutionJob completed");
        } catch (Exception e) {
            log.error("PaymentRunAutoExecutionJob failed: {}", e.getMessage(), e);
        }
    }
}
