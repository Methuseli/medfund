package com.medfund.user.scheduler;

/**
 * @deprecated Replaced by {@link com.medfund.user.job.AgeProcessingExecutor}
 * which is tenant-configurable via scheduled_job_configs table.
 * This class is kept empty to avoid breaking existing test imports.
 * The old @Scheduled annotation has been removed.
 */
public class MemberAgeProcessingJob {
    // Replaced by tenant-configurable AgeProcessingExecutor
}
