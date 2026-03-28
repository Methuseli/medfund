package com.medfund.claims.scheduler;

import com.medfund.claims.service.PreAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PreAuthExpiryJob {
    private static final Logger log = LoggerFactory.getLogger(PreAuthExpiryJob.class);
    private final PreAuthService preAuthService;

    public PreAuthExpiryJob(PreAuthService preAuthService) {
        this.preAuthService = preAuthService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void execute() {
        log.info("Starting PreAuthExpiryJob");
        try {
            preAuthService.expireApprovedPastDate().block();
            log.info("PreAuthExpiryJob completed");
        } catch (Exception e) {
            log.error("PreAuthExpiryJob failed: {}", e.getMessage(), e);
        }
    }
}
