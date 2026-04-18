-- Platform-level staff users — stored once in the public schema, not per-tenant.
-- These are admin/operational users who manage the platform (not insurance members).

CREATE TABLE IF NOT EXISTS public.staff_users (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    email             VARCHAR(255) NOT NULL UNIQUE,
    phone             VARCHAR(50),
    job_title         VARCHAR(100),
    department        VARCHAR(100),
    realm_role        VARCHAR(100) NOT NULL,
    keycloak_user_id  VARCHAR(255),
    status            VARCHAR(20)  NOT NULL DEFAULT 'active',
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        UUID,
    updated_by        UUID
);

CREATE INDEX IF NOT EXISTS idx_staff_users_email      ON public.staff_users(email);
CREATE INDEX IF NOT EXISTS idx_staff_users_status     ON public.staff_users(status);
CREATE INDEX IF NOT EXISTS idx_staff_users_realm_role ON public.staff_users(realm_role);
