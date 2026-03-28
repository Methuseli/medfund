package com.medfund.shared.scheduler;

import reactor.core.publisher.Mono;

/**
 * Interface for tenant-specific job executors.
 * Each service registers implementations for its domain job types.
 */
public interface JobExecutor {

    /**
     * The job type this executor handles.
     */
    JobType getJobType();

    /**
     * Execute the job for a specific tenant with the given settings.
     *
     * @param tenantId the tenant identifier
     * @param settings JSON string of job-specific configuration
     * @return Mono that completes when the job finishes
     */
    Mono<Void> execute(String tenantId, String settings);
}
