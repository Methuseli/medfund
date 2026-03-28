package com.medfund.claims.job;

import com.medfund.claims.service.TariffService;
import com.medfund.shared.scheduler.JobExecutor;
import com.medfund.shared.scheduler.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TariffActivationExecutor implements JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(TariffActivationExecutor.class);
    private final TariffService tariffService;

    public TariffActivationExecutor(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @Override
    public JobType getJobType() { return JobType.TARIFF_ACTIVATION; }

    @Override
    public Mono<Void> execute(String tenantId, String settings) {
        log.info("Executing tariff activation check for tenant: {}", tenantId);
        return tariffService.activateSchedulesByDate()
            .then(tariffService.deactivateExpiredSchedules());
    }
}
