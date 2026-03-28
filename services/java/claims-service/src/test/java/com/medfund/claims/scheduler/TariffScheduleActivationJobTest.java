package com.medfund.claims.scheduler;

import com.medfund.claims.service.TariffService;
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
class TariffScheduleActivationJobTest {
    @Mock TariffService tariffService;
    @InjectMocks TariffScheduleActivationJob job;

    @Test
    void execute_callsService() {
        when(tariffService.activateSchedulesByDate()).thenReturn(Mono.empty());
        when(tariffService.deactivateExpiredSchedules()).thenReturn(Mono.empty());
        job.execute();
        verify(tariffService).activateSchedulesByDate();
        verify(tariffService).deactivateExpiredSchedules();
    }

    @Test
    void execute_serviceFailure_doesNotThrow() {
        when(tariffService.activateSchedulesByDate()).thenReturn(Mono.error(new RuntimeException("DB down")));
        assertDoesNotThrow(() -> job.execute());
    }
}
