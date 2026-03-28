package com.medfund.user.scheduler;

import com.medfund.user.service.DependantService;
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
class MemberAgeProcessingJobTest {
    @Mock DependantService dependantService;
    @InjectMocks MemberAgeProcessingJob job;

    @Test
    void execute_callsService() {
        when(dependantService.flagOverAgeDependants()).thenReturn(Mono.empty());
        job.execute();
        verify(dependantService).flagOverAgeDependants();
    }

    @Test
    void execute_serviceFailure_doesNotThrow() {
        when(dependantService.flagOverAgeDependants()).thenReturn(Mono.error(new RuntimeException("DB down")));
        assertDoesNotThrow(() -> job.execute());
    }
}
