-- Baseline tenant schema — applied to every new tenant schema on provisioning.
-- All tables here are tenant-scoped (one copy per tenant schema).

-- ── Members & Groups ────────────────────────────────────────────

CREATE TABLE groups (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200) NOT NULL,
    registration_number VARCHAR(100),
    contact_person  VARCHAR(200),
    contact_email   VARCHAR(255),
    contact_phone   VARCHAR(50),
    address         TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

CREATE TABLE members (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_number   VARCHAR(50) NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    date_of_birth   DATE NOT NULL,
    gender          VARCHAR(10),
    national_id     VARCHAR(50),
    email           VARCHAR(255),
    phone           VARCHAR(50),
    address         TEXT,
    group_id        UUID REFERENCES groups(id),
    scheme_id       UUID,
    keycloak_user_id VARCHAR(255),
    status          VARCHAR(20) NOT NULL DEFAULT 'active',
    enrollment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    termination_date DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

CREATE TABLE dependants (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id       UUID NOT NULL REFERENCES members(id),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    date_of_birth   DATE NOT NULL,
    gender          VARCHAR(10),
    relationship    VARCHAR(50) NOT NULL,
    national_id     VARCHAR(50),
    status          VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

-- ── Providers ───────────────────────────────────────────────────

CREATE TABLE providers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200) NOT NULL,
    practice_number VARCHAR(50),
    ahfoz_number    VARCHAR(50),
    specialty       VARCHAR(100),
    email           VARCHAR(255),
    phone           VARCHAR(50),
    address         TEXT,
    banking_details JSONB DEFAULT '{}',
    keycloak_user_id VARCHAR(255),
    status          VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

-- ── Schemes & Benefits ──────────────────────────────────────────

CREATE TABLE schemes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    scheme_type     VARCHAR(50) NOT NULL DEFAULT 'medical_aid',
    status          VARCHAR(20) NOT NULL DEFAULT 'active',
    effective_date  DATE NOT NULL,
    end_date        DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

CREATE TABLE scheme_benefits (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheme_id       UUID NOT NULL REFERENCES schemes(id),
    name            VARCHAR(200) NOT NULL,
    benefit_type    VARCHAR(50) NOT NULL,
    annual_limit    DECIMAL(19,4),
    daily_limit     DECIMAL(19,4),
    event_limit     DECIMAL(19,4),
    currency_code   CHAR(3) NOT NULL DEFAULT 'USD',
    waiting_period_days INTEGER DEFAULT 0,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE age_groups (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheme_id       UUID NOT NULL REFERENCES schemes(id),
    name            VARCHAR(100) NOT NULL,
    min_age         INTEGER NOT NULL,
    max_age         INTEGER NOT NULL,
    contribution_amount DECIMAL(19,4) NOT NULL,
    currency_code   CHAR(3) NOT NULL DEFAULT 'USD',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Claims ──────────────────────────────────────────────────────

CREATE TABLE claims (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_number    VARCHAR(50) NOT NULL UNIQUE,
    member_id       UUID NOT NULL REFERENCES members(id),
    dependant_id    UUID REFERENCES dependants(id),
    provider_id     UUID NOT NULL REFERENCES providers(id),
    scheme_id       UUID NOT NULL REFERENCES schemes(id),
    benefit_id      UUID REFERENCES scheme_benefits(id),
    claim_type      VARCHAR(50) NOT NULL DEFAULT 'medical',
    status          VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    service_date    DATE NOT NULL,
    submission_date TIMESTAMPTZ,
    claimed_amount  DECIMAL(19,4) NOT NULL,
    approved_amount DECIMAL(19,4),
    paid_amount     DECIMAL(19,4),
    currency_code   CHAR(3) NOT NULL DEFAULT 'USD',
    diagnosis_codes JSONB DEFAULT '[]',
    procedure_codes JSONB DEFAULT '[]',
    notes           TEXT,
    rejection_reason VARCHAR(100),
    rejection_notes TEXT,
    verification_code VARCHAR(20),
    verified_at     TIMESTAMPTZ,
    adjudicated_at  TIMESTAMPTZ,
    adjudicated_by  UUID,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

CREATE TABLE claim_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_id        UUID NOT NULL REFERENCES claims(id),
    tariff_code     VARCHAR(20) NOT NULL,
    description     TEXT,
    quantity        INTEGER NOT NULL DEFAULT 1,
    unit_price      DECIMAL(19,4) NOT NULL,
    claimed_amount  DECIMAL(19,4) NOT NULL,
    approved_amount DECIMAL(19,4),
    modifier_codes  JSONB DEFAULT '[]',
    currency_code   CHAR(3) NOT NULL DEFAULT 'USD',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Tariffs ─────────────────────────────────────────────────────

CREATE TABLE tariff_schedules (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200) NOT NULL,
    effective_date  DATE NOT NULL,
    end_date        DATE,
    source          VARCHAR(100),
    status          VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE tariff_codes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    schedule_id     UUID NOT NULL REFERENCES tariff_schedules(id),
    code            VARCHAR(20) NOT NULL,
    description     TEXT NOT NULL,
    category        VARCHAR(100),
    unit_price      DECIMAL(19,4) NOT NULL,
    currency_code   CHAR(3) NOT NULL DEFAULT 'USD',
    requires_pre_auth BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Contributions & Finance ─────────────────────────────────────

CREATE TABLE contributions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id       UUID REFERENCES members(id),
    group_id        UUID REFERENCES groups(id),
    scheme_id       UUID NOT NULL REFERENCES schemes(id),
    amount          DECIMAL(19,4) NOT NULL,
    currency_code   CHAR(3) NOT NULL DEFAULT 'USD',
    period_start    DATE NOT NULL,
    period_end      DATE NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'pending',
    payment_method  VARCHAR(50),
    payment_reference VARCHAR(100),
    paid_at         TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

CREATE TABLE payments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_number  VARCHAR(50) NOT NULL UNIQUE,
    provider_id     UUID REFERENCES providers(id),
    amount          DECIMAL(19,4) NOT NULL,
    currency_code   CHAR(3) NOT NULL DEFAULT 'USD',
    payment_type    VARCHAR(50) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'pending',
    payment_method  VARCHAR(50),
    reference       VARCHAR(200),
    paid_at         TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

-- ── Waiting Periods ─────────────────────────────────────────────

CREATE TABLE waiting_period_rules (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheme_id       UUID NOT NULL REFERENCES schemes(id),
    condition_type  VARCHAR(50) NOT NULL,
    waiting_days    INTEGER NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Business Rules ──────────────────────────────────────────────

CREATE TABLE business_rules (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200) NOT NULL,
    category        VARCHAR(50) NOT NULL,
    description     TEXT,
    rule_definition JSONB NOT NULL,
    compiled_drl    TEXT,
    version         INTEGER NOT NULL DEFAULT 1,
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    priority        INTEGER NOT NULL DEFAULT 100,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

-- ── Roles & Permissions ─────────────────────────────────────────

CREATE TABLE roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL UNIQUE,
    display_name    VARCHAR(200) NOT NULL,
    description     TEXT,
    is_system       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE role_permissions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission      VARCHAR(100) NOT NULL,
    access_level    VARCHAR(20) NOT NULL DEFAULT 'full',
    UNIQUE(role_id, permission)
);

CREATE TABLE user_roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    assigned_by     UUID,
    UNIQUE(user_id, role_id)
);

-- ── Branding ────────────────────────────────────────────────────

CREATE TABLE branding_config (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key      VARCHAR(100) NOT NULL UNIQUE,
    config_value    TEXT NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by      UUID
);

-- ── Notification Templates ──────────────────────────────────────

CREATE TABLE notification_templates (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL UNIQUE,
    channel         VARCHAR(20) NOT NULL,
    subject         VARCHAR(500),
    body_template   TEXT NOT NULL,
    variables       JSONB DEFAULT '[]',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── AI Predictions ──────────────────────────────────────────────

CREATE TABLE ai_predictions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       UUID NOT NULL,
    prediction_type VARCHAR(50) NOT NULL,
    model_version   VARCHAR(50) NOT NULL,
    input_features  JSONB NOT NULL,
    output          JSONB NOT NULL,
    confidence      DECIMAL(5,4) NOT NULL,
    accepted        BOOLEAN,
    reviewed_by     UUID,
    reviewed_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Indexes ─────────────────────────────────────────────────────

CREATE INDEX idx_members_group ON members(group_id);
CREATE INDEX idx_members_scheme ON members(scheme_id);
CREATE INDEX idx_members_status ON members(status);
CREATE INDEX idx_claims_member ON claims(member_id);
CREATE INDEX idx_claims_provider ON claims(provider_id);
CREATE INDEX idx_claims_status ON claims(status);
CREATE INDEX idx_claims_service_date ON claims(service_date);
CREATE INDEX idx_contributions_member ON contributions(member_id);
CREATE INDEX idx_contributions_group ON contributions(group_id);
CREATE INDEX idx_contributions_status ON contributions(status);
CREATE INDEX idx_payments_provider ON payments(provider_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_tariff_codes_schedule ON tariff_codes(schedule_id);
CREATE INDEX idx_tariff_codes_code ON tariff_codes(code);
CREATE INDEX idx_business_rules_category ON business_rules(category);
CREATE INDEX idx_business_rules_status ON business_rules(status);
CREATE INDEX idx_ai_predictions_entity ON ai_predictions(entity_type, entity_id);
