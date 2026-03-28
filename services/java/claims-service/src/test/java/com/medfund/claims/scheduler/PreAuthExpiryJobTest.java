package com.medfund.claims.scheduler;

import com.medfund.claims.job.PreAuthExpiryExecutor;
import com.medfund.claims.service.PreAuthService;
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
class PreAuthExpiryJobTest {
    @Mock PreAuthService preAuthService;

    @Test
    void execute_callsExpireApprovedPastDate() {
        when(preAuthService.expireApprovedPastDate()).thenReturn(Mono.empty());
        var executor = new PreAuthExpiryExecutor(preAuthService);
        assertThat(executor.getJobType()).isEqualTo(JobType.PRE_AUTH_EXPIRY);

        StepVerifier.create(executor.execute("test-tenant", "{}"))
            .verifyComplete();
        verify(preAuthService).expireApprovedPastDate();
    }
}
