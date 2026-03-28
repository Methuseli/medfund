package com.medfund.shared.scheduler;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

class JobDispatcherTest {

    @Test
    void calculateNextExecution_validCron_returnsNextTime() {
        Instant next = JobDispatcher.calculateNextExecution("0 0 2 * * *"); // daily 2am
        assertThat(next).isNotNull();
        assertThat(next).isAfter(Instant.now());
    }

    @Test
    void calculateNextExecution_invalidCron_returnsFallback() {
        Instant next = JobDispatcher.calculateNextExecution("invalid-cron");
        assertThat(next).isNotNull();
        assertThat(next).isAfter(Instant.now());
    }

    @Test
    void calculateNextExecution_monthlyCron_returnsNextMonth() {
        Instant next = JobDispatcher.calculateNextExecution("0 0 10 15 * *"); // 15th at 10am
        assertThat(next).isNotNull();
        assertThat(next).isAfter(Instant.now());
    }
}
