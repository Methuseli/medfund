package com.medfund.claims.scheduler;

import com.medfund.claims.job.TariffActivationExecutor;
import com.medfund.claims.service.TariffService;
import com.medfund.shared.scheduler.JobType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffScheduleActivationJobTest {
    @Mock TariffService tariffService;

    @Test
    void execute_callsBothActivateAndDeactivate() {
        when(tariffService.activateSchedulesByDate()).thenReturn(Mono.empty());
        when(tariffService.deactivateExpiredSchedules()).thenReturn(Mono.empty());
        var executor = new TariffActivationExecutor(tariffService);
        assertThat(executor.getJobType()).isEqualTo(JobType.TARIFF_ACTIVATION);

        StepVerifier.create(executor.execute("test-tenant", "{}"))
            .verifyComplete();
        verify(tariffService).activateSchedulesByDate();
        verify(tariffService).deactivateExpiredSchedules();
    }
}
