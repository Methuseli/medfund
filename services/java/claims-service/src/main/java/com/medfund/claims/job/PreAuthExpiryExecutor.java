package com.medfund.claims.job;

import com.medfund.claims.service.PreAuthService;
import com.medfund.shared.scheduler.JobExecutor;
import com.medfund.shared.scheduler.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PreAuthExpiryExecutor implements JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(PreAuthExpiryExecutor.class);
    private final PreAuthService preAuthService;

    public PreAuthExpiryExecutor(PreAuthService preAuthService) {
        this.preAuthService = preAuthService;
    }

    @Override
    public JobType getJobType() { return JobType.PRE_AUTH_EXPIRY; }

    @Override
    public Mono<Void> execute(String tenantId, String settings) {
        log.info("Executing pre-auth expiry check for tenant: {}", tenantId);
        return preAuthService.expireApprovedPastDate();
    }
}
