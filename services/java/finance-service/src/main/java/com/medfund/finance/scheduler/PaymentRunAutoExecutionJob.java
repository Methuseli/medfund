package com.medfund.finance.scheduler;

/**
 * @deprecated Replaced by {@link com.medfund.finance.job.PaymentRunExecutor}
 * which is tenant-configurable via scheduled_job_configs table.
 * This class is kept empty to avoid breaking existing test imports.
 * The old @Scheduled annotation has been removed.
 */
public class PaymentRunAutoExecutionJob {
    // Replaced by tenant-configurable PaymentRunExecutor
}
