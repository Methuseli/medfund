package com.medfund.claims.scheduler;

/**
 * @deprecated Replaced by {@link com.medfund.claims.job.TariffActivationExecutor}
 * which is tenant-configurable via scheduled_job_configs table.
 * This class is kept empty to avoid breaking existing test imports.
 * The old @Scheduled annotation has been removed.
 */
public class TariffScheduleActivationJob {
    // Replaced by tenant-configurable TariffActivationExecutor
}
