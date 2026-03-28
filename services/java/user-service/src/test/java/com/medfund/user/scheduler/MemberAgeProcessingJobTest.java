package com.medfund.user.scheduler;

import com.medfund.user.job.AgeProcessingExecutor;
import com.medfund.user.service.DependantService;
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
class MemberAgeProcessingJobTest {
    @Mock DependantService dependantService;

    @Test
    void execute_callsFlagOverAgeDependants() {
        when(dependantService.flagOverAgeDependants()).thenReturn(Mono.empty());
        var executor = new AgeProcessingExecutor(dependantService, new ObjectMapper());
        assertThat(executor.getJobType()).isEqualTo(JobType.AGE_PROCESSING);

        StepVerifier.create(executor.execute("test-tenant", "{\"maxDependantAge\":21,\"maxStudentAge\":26}"))
            .verifyComplete();
        verify(dependantService).flagOverAgeDependants();
    }
}
