package com.medfund.finance.scheduler;

import com.medfund.finance.job.PaymentRunExecutor;
import com.medfund.finance.service.PaymentRunService;
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
class PaymentRunAutoExecutionJobTest {
    @Mock PaymentRunService paymentRunService;

    @Test
    void execute_callsAutoExecuteDraftRuns() {
        when(paymentRunService.autoExecuteDraftRuns()).thenReturn(Mono.empty());
        var executor = new PaymentRunExecutor(paymentRunService, new ObjectMapper());
        assertThat(executor.getJobType()).isEqualTo(JobType.PAYMENT_RUN);

        StepVerifier.create(executor.execute("test-tenant", "{\"autoExecuteAfterHours\":24}"))
            .verifyComplete();
        verify(paymentRunService).autoExecuteDraftRuns();
    }
}
