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
class BillingCycleJobTest {
    @Mock BillingService billingService;
    @InjectMocks BillingCycleJob job;

    @Test
    void execute_callsService() {
        when(billingService.runAutoBilling()).thenReturn(Mono.empty());
        job.execute();
        verify(billingService).runAutoBilling();
    }

    @Test
    void execute_serviceFailure_doesNotThrow() {
        when(billingService.runAutoBilling()).thenReturn(Mono.error(new RuntimeException("DB down")));
        assertDoesNotThrow(() -> job.execute());
    }
}
