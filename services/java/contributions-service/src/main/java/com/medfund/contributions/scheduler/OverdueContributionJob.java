package com.medfund.contributions.scheduler;

/**
 * @deprecated Replaced by {@link com.medfund.contributions.job.OverdueCheckExecutor}
 * which is tenant-configurable via scheduled_job_configs table.
 * This class is kept empty to avoid breaking existing test imports.
 * The old @Scheduled annotation has been removed.
 */
public class OverdueContributionJob {
    // Replaced by tenant-configurable OverdueCheckExecutor
}
