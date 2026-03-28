package com.medfund.contributions.scheduler;

import com.medfund.contributions.job.OverdueCheckExecutor;
import com.medfund.contributions.service.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medfund.shared.scheduler.JobType;

@ExtendWith(MockitoExtension.class)
class OverdueContributionJobTest {
    @Mock BillingService billingService;

    @Test
    void execute_callsMarkOverdueContributions() {
        when(billingService.markOverdueContributions()).thenReturn(Mono.empty());
        var executor = new OverdueCheckExecutor(billingService, new ObjectMapper());
        assertThat(executor.getJobType()).isEqualTo(JobType.OVERDUE_CHECK);

        StepVerifier.create(executor.execute("test-tenant", "{\"gracePeriodDays\":30}"))
            .verifyComplete();
        verify(billingService).markOverdueContributions();
    }

    @Test
    void execute_withInvalidSettings_stillExecutes() {
        when(billingService.markOverdueContributions()).thenReturn(Mono.empty());
        var executor = new OverdueCheckExecutor(billingService, new ObjectMapper());

        StepVerifier.create(executor.execute("test-tenant", "invalid-json"))
            .verifyComplete();
        verify(billingService).markOverdueContributions();
    }
}
