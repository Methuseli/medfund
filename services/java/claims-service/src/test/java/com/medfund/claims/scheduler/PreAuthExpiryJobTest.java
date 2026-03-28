package com.medfund.claims.scheduler;

import com.medfund.claims.service.PreAuthService;
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
class PreAuthExpiryJobTest {
    @Mock PreAuthService preAuthService;
    @InjectMocks PreAuthExpiryJob job;

    @Test
    void execute_callsService() {
        when(preAuthService.expireApprovedPastDate()).thenReturn(Mono.empty());
        job.execute();
        verify(preAuthService).expireApprovedPastDate();
    }

    @Test
    void execute_serviceFailure_doesNotThrow() {
        when(preAuthService.expireApprovedPastDate()).thenReturn(Mono.error(new RuntimeException("DB down")));
        assertDoesNotThrow(() -> job.execute());
    }
}
