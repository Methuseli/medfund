package com.medfund.claims.scheduler;

import com.medfund.claims.service.TariffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TariffScheduleActivationJob {
    private static final Logger log = LoggerFactory.getLogger(TariffScheduleActivationJob.class);
    private final TariffService tariffService;

    public TariffScheduleActivationJob(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void execute() {
        log.info("Starting TariffScheduleActivationJob");
        try {
            tariffService.activateSchedulesByDate().block();
            tariffService.deactivateExpiredSchedules().block();
            log.info("TariffScheduleActivationJob completed");
        } catch (Exception e) {
            log.error("TariffScheduleActivationJob failed: {}", e.getMessage(), e);
        }
    }
}
