package com.medfund.finance.scheduler;

import com.medfund.finance.service.PaymentRunService;
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
class PaymentRunAutoExecutionJobTest {
    @Mock PaymentRunService paymentRunService;
    @InjectMocks PaymentRunAutoExecutionJob job;

    @Test
    void execute_callsService() {
        when(paymentRunService.autoExecuteDraftRuns()).thenReturn(Mono.empty());
        job.execute();
        verify(paymentRunService).autoExecuteDraftRuns();
    }

    @Test
    void execute_serviceFailure_doesNotThrow() {
        when(paymentRunService.autoExecuteDraftRuns()).thenReturn(Mono.error(new RuntimeException("DB down")));
        assertDoesNotThrow(() -> job.execute());
    }
}
