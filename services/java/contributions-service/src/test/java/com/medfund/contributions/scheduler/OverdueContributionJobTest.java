package com.medfund.contributions.scheduler;

import com.medfund.contributions.service.BillingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OverdueContributionJobTest {
    @Mock BillingService billingService;
    @InjectMocks OverdueContributionJob job;

    @Test
    void execute_callsService() {
        when(billingService.markOverdueContributions()).thenReturn(Mono.empty());
        job.execute();
        verify(billingService).markOverdueContributions();
    }

    @Test
    void execute_serviceFailure_doesNotThrow() {
        when(billingService.markOverdueContributions()).thenReturn(Mono.error(new RuntimeException("DB down")));
        assertDoesNotThrow(() -> job.execute());
    }
}
