package com.medfund.user.scheduler;

import com.medfund.user.service.DependantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MemberAgeProcessingJob {
    private static final Logger log = LoggerFactory.getLogger(MemberAgeProcessingJob.class);
    private final DependantService dependantService;

    public MemberAgeProcessingJob(DependantService dependantService) {
        this.dependantService = dependantService;
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void execute() {
        log.info("Starting MemberAgeProcessingJob");
        try {
            dependantService.flagOverAgeDependants().block();
            log.info("MemberAgeProcessingJob completed");
        } catch (Exception e) {
            log.error("MemberAgeProcessingJob failed: {}", e.getMessage(), e);
        }
    }
}
