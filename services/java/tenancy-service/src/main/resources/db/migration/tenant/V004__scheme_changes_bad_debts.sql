-- Scheme change workflow
CREATE TABLE scheme_changes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id       UUID NOT NULL REFERENCES members(id),
    from_scheme_id  UUID NOT NULL REFERENCES schemes(id),
    to_scheme_id    UUID NOT NULL REFERENCES schemes(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_date  DATE NOT NULL DEFAULT CURRENT_DATE,
    effective_date  DATE NOT NULL,
    reason          TEXT,
    rejection_reason TEXT,
    approved_by     UUID,
    approved_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      UUID
);

CREATE INDEX idx_scheme_changes_member ON scheme_changes(member_id);
CREATE INDEX idx_scheme_changes_status ON scheme_changes(status);

-- Bad debt tracking
CREATE TABLE bad_debts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contribution_id UUID REFERENCES contributions(id),
    member_id       UUID REFERENCES members(id),
    group_id        UUID REFERENCES groups(id),
    amount          DECIMAL(19,4) NOT NULL,
    currency_code   CHAR(3) NOT NULL DEFAULT 'USD',
    status          VARCHAR(20) NOT NULL DEFAULT 'FLAGGED',
    reason          TEXT,
    flagged_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    written_off_date DATE,
    written_off_by  UUID,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bad_debts_member ON bad_debts(member_id);
CREATE INDEX idx_bad_debts_status ON bad_debts(status);
