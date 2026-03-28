-- Tenant-configurable scheduled job configurations
CREATE TABLE scheduled_job_configs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_type            VARCHAR(50) NOT NULL,
    name                VARCHAR(200) NOT NULL,
    cron_expression     VARCHAR(100) NOT NULL,
    is_enabled          BOOLEAN NOT NULL DEFAULT TRUE,
    settings            JSONB NOT NULL DEFAULT '{}',
    last_executed_at    TIMESTAMPTZ,
    next_execution_at   TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          UUID
);

CREATE INDEX idx_scheduled_jobs_type ON scheduled_job_configs(job_type);
CREATE INDEX idx_scheduled_jobs_next ON scheduled_job_configs(next_execution_at) WHERE is_enabled = true;
