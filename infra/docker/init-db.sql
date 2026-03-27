-- Initialize schemas for local development
CREATE SCHEMA IF NOT EXISTS keycloak;
CREATE SCHEMA IF NOT EXISTS analytics;

-- Public schema tables (shared across tenants)
CREATE TABLE IF NOT EXISTS public.plans (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL,
    max_members     INTEGER,
    max_providers   INTEGER,
    max_storage_gb  INTEGER,
    features        JSONB NOT NULL DEFAULT '{}',
    price           DECIMAL(19,4),
    currency_code   CHAR(3),
    billing_cycle   VARCHAR(20),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.tenants (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name              VARCHAR(200) NOT NULL,
    slug              VARCHAR(100) NOT NULL UNIQUE,
    domain            VARCHAR(255),
    schema_name       VARCHAR(100) NOT NULL UNIQUE,
    plan_id           UUID REFERENCES public.plans(id),
    status            VARCHAR(20) NOT NULL DEFAULT 'active',
    settings          JSONB NOT NULL DEFAULT '{}',
    branding          JSONB NOT NULL DEFAULT '{}',
    contact_email     VARCHAR(255) NOT NULL,
    country_code      CHAR(2) NOT NULL,
    timezone          VARCHAR(50) NOT NULL DEFAULT 'UTC',
    membership_model  VARCHAR(20) NOT NULL DEFAULT 'BOTH',
    keycloak_realm    VARCHAR(100) NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.currencies (
    code          CHAR(3) PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    symbol        VARCHAR(10) NOT NULL,
    decimal_places INTEGER NOT NULL DEFAULT 2,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS public.exchange_rates (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_currency CHAR(3) NOT NULL REFERENCES public.currencies(code),
    target_currency CHAR(3) NOT NULL REFERENCES public.currencies(code),
    rate            DECIMAL(19,10) NOT NULL,
    effective_date  DATE NOT NULL,
    source          VARCHAR(50) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Seed default currencies
INSERT INTO public.currencies (code, name, symbol, decimal_places) VALUES
    ('USD', 'United States Dollar', '$', 2),
    ('ZWG', 'Zimbabwe Gold', 'ZiG', 2),
    ('ZAR', 'South African Rand', 'R', 2),
    ('BWP', 'Botswana Pula', 'P', 2),
    ('GBP', 'British Pound', '£', 2),
    ('EUR', 'Euro', '€', 2)
ON CONFLICT (code) DO NOTHING;

-- Seed default plans
INSERT INTO public.plans (name, max_members, max_providers, max_storage_gb, features, price, currency_code, billing_cycle) VALUES
    ('Starter', 2000, 50, 10, '{"ai_adjudication": false, "chat": false, "fraud_detection": false}', 500.0000, 'USD', 'monthly'),
    ('Professional', 15000, 200, 50, '{"ai_adjudication": true, "chat": true, "fraud_detection": true}', 3000.0000, 'USD', 'monthly'),
    ('Enterprise', NULL, NULL, NULL, '{"ai_adjudication": true, "chat": true, "fraud_detection": true, "custom_domain": true, "dedicated_support": true}', 10000.0000, 'USD', 'monthly')
ON CONFLICT DO NOTHING;
