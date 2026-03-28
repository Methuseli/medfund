-- Insurance quotes for prospective members
CREATE TABLE insurance_quotes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_number    VARCHAR(50) NOT NULL UNIQUE,
    scheme_id       UUID NOT NULL REFERENCES schemes(id),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    date_of_birth   DATE NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(50),
    dependant_count INTEGER DEFAULT 0,
    member_premium  DECIMAL(19,4),
    total_premium   DECIMAL(19,4),
    currency_code   CHAR(3) DEFAULT 'USD',
    quote_details   JSONB DEFAULT '{}',
    status          VARCHAR(20) NOT NULL DEFAULT 'GENERATED',
    valid_until     DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_insurance_quotes_number ON insurance_quotes(quote_number);
CREATE INDEX idx_insurance_quotes_email ON insurance_quotes(email);
