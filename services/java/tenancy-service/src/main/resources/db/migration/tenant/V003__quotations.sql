-- Quotation request flow — pre-treatment cost estimates
CREATE TABLE quotations (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_number    VARCHAR(50) NOT NULL UNIQUE,
    member_id           UUID NOT NULL REFERENCES members(id),
    provider_id         UUID NOT NULL REFERENCES providers(id),
    scheme_id           UUID NOT NULL REFERENCES schemes(id),
    diagnosis_code      VARCHAR(20),
    procedure_codes     JSONB DEFAULT '[]',
    description         TEXT NOT NULL,
    estimated_amount    DECIMAL(19,4) NOT NULL,
    covered_amount      DECIMAL(19,4),
    co_payment_amount   DECIMAL(19,4),
    currency_code       CHAR(3) NOT NULL DEFAULT 'USD',
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    valid_until         DATE,
    reviewed_by         UUID,
    reviewed_at         TIMESTAMPTZ,
    notes               TEXT,
    rejection_reason    TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          UUID
);

CREATE INDEX idx_quotations_member ON quotations(member_id);
CREATE INDEX idx_quotations_provider ON quotations(provider_id);
CREATE INDEX idx_quotations_status ON quotations(status);
