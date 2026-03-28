package com.medfund.contributions.scheduler;

import com.medfund.contributions.job.BillingCycleExecutor;
import com.medfund.contributions.service.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class BillingCycleJobTest {
    @Mock BillingService billingService;

    @Test
    void execute_callsRunAutoBilling() {
        when(billingService.runAutoBilling()).thenReturn(Mono.empty());
        var executor = new BillingCycleExecutor(billingService, new ObjectMapper());
        assertThat(executor.getJobType()).isEqualTo(JobType.BILLING_CYCLE);

        StepVerifier.create(executor.execute("test-tenant", "{\"billingDayOfMonth\":15}"))
            .verifyComplete();
        verify(billingService).runAutoBilling();
    }

    @Test
    void execute_withInvalidSettings_stillExecutes() {
        var executor = new BillingCycleExecutor(billingService, new ObjectMapper());
        // Invalid JSON triggers the catch block which returns Mono.empty()
        StepVerifier.create(executor.execute("test-tenant", "invalid"))
            .verifyComplete();
    }
}
