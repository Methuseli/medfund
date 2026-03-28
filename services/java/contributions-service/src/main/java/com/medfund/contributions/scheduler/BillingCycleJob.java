package com.medfund.contributions.scheduler;

/**
 * @deprecated Replaced by {@link com.medfund.contributions.job.BillingCycleExecutor}
 * which is tenant-configurable via scheduled_job_configs table.
 * This class is kept empty to avoid breaking existing test imports.
 * The old @Scheduled annotation has been removed.
 */
public class BillingCycleJob {
    // Replaced by tenant-configurable BillingCycleExecutor
}
