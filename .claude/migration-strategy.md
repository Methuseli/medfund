# Build Strategy

## Context

The legacy system has been decommissioned. This is a **greenfield build** — there is no running system to migrate from, no traffic to shift, and no legacy data to migrate. The legacy codebase in this repository serves only as a **domain knowledge reference** for understanding the business rules, data models, and workflows that the new platform must support.

## Guiding Principles

1. **Build vertically, not horizontally.** Deliver one complete feature slice (backend + frontend + infra) end-to-end before starting the next.
2. **Multi-tenancy from day one.** Every service is tenant-aware from the first line of code.
3. **AI-ready from day one.** The AI Service and prediction pipeline are part of the core architecture, not an afterthought.
4. **Infrastructure first.** CI/CD, observability, and deployment pipelines are set up before writing business logic.
5. **Maximum parallelization.** Four workstreams run concurrently after foundation is complete.

## Timeline Overview — 22 Weeks (~5.5 Months)

```
Week:  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22
       ├──────────┤
       Phase 0: Foundation

                  ├─────────────────────────────────────────────────────┤
                  Stream A (Java): Tenancy → Users → Claims → Contrib → Finance → Rules

                        ├────────────────────────────────┤
                        Stream B (Go): Gateway → Notif → Audit → Files

                              ├──────────────────────────────────┤
                              Stream C (Frontend): Angular + Flutter

                                          ├──────────────────────┤
                                          Stream D (Elixir+Python): Real-time → AI

                                                                        ├────┤
                                                                        Phase 5: Harden + Launch
```

## Phase 0: Foundation (Weeks 1-3)

### Goals
- Monorepo scaffold, CI/CD, shared infra, Keycloak, base DB schema

### Tasks
- [ ] Initialize monorepo structure (all service stubs across 4 languages)
- [ ] `docker-compose.yml` for local dev (PostgreSQL 17, Redis 7, Kafka, Keycloak, MinIO)
- [ ] Helm charts for staging (PostgreSQL, Redis, Kafka, Keycloak, MinIO)
- [ ] Terraform base for cloud resources (VPC, EKS, RDS, ElastiCache, MSK, ECR, S3)
- [ ] GitHub Actions CI per language (build, test, lint, scan, container push)
- [ ] ArgoCD setup for staging
- [ ] Keycloak:
  - [ ] Platform realm (super admin)
  - [ ] Template tenant realm (roles, MFA policies, OIDC clients for Angular + Flutter)
  - [ ] OAuth 2.0 Authorization Code + PKCE configuration
  - [ ] MFA: TOTP, Email OTP, SMS OTP policies
- [ ] Protobuf definitions (gRPC contracts)
- [ ] Kafka topics + Avro schemas for events
- [ ] `public` schema: tenants, currencies, exchange_rates, plans tables
- [ ] Flyway baseline migration template for tenant schemas
- [ ] Gradle multi-project (Java), Go workspace, Mix umbrella (Elixir), uv project (Python)

---

## Stream A: Java Services (Weeks 3-18)

The core domain services, built sequentially because each depends on the previous.

### A1. Tenancy Service (Weeks 3-5)
- [ ] Tenant CRUD in `public` schema (R2DBC reactive)
- [ ] Schema provisioning (`CREATE SCHEMA tenant_{uuid}` + Flyway migrations)
- [ ] Tenant resolution WebFilter (subdomain / JWT claim → tenant_id)
- [ ] Tenant-aware R2DBC connection factory (`SET search_path` from Reactor context)
- [ ] Keycloak realm provisioning per tenant (Admin API)
- [ ] Super admin endpoints (Swagger documented, audit-logged)
- [ ] Plan and feature flag management
- [ ] Per-tenant currency configuration

### A2. User Service (Weeks 5-7)
- [ ] Member, Dependant, Provider, Group R2DBC entities
- [ ] Keycloak sync (user creation → Keycloak realm)
- [ ] Member lifecycle (enroll → activate → suspend → terminate)
- [ ] Provider onboarding + AHFOZ verification
- [ ] Banking details, waiting period tracking, group management
- [ ] Role/permission assignment (syncs with Keycloak roles)
- [ ] All endpoints Swagger documented, audit-logged

### A3. Claims Service (Weeks 7-12)
- [ ] Claim submission reactive pipeline + claim state machine
- [ ] Claim verification (code generation + validation)
- [ ] Pre-authorization workflow
- [ ] Tariff management (schedule CRUD, bulk import, versioning)
- [ ] Tariff modifiers (application logic, bilateral, after-hours, assistant, etc.)
- [ ] ICD-10 registry + diagnosis-procedure mapping
- [ ] AHFOZ specialty ↔ tariff code validation
- [ ] 6-stage adjudication pipeline (see adjudication.md):
  - [ ] Eligibility → Waiting periods → Benefit limits → Pre-auth → Tariff/pricing → Clinical
- [ ] Balance checking, rejection reasons, co-payment, credit claims, special waivers
- [ ] Drug claims (same pipeline, separate tariff codes)
- [ ] All endpoints Swagger documented, audit-logged

### A4. Contributions Service (Weeks 10-13)
- [ ] Scheme + benefit management, age group pricing
- [ ] Contribution billing cycle automation (configurable per tenant)
- [ ] Group/member contribution generation
- [ ] Transaction recording (multi-currency, multiple payment methods)
- [ ] Balance tracking (group, member, benefit-level, per currency)
- [ ] Invoice generation, scheme change workflows, bad debt tracking
- [ ] All endpoints Swagger documented, audit-logged

### A5. Finance Service (Weeks 12-15)
- [ ] Payment processing (multi-currency)
- [ ] Payment run creation + execution
- [ ] Provider balance management (per currency)
- [ ] Adjustments, debit/credit notes, bank reconciliation
- [ ] Payment advice generation, financial reporting queries
- [ ] All endpoints Swagger documented, audit-logged

### A6. Rules Engine (Weeks 8-15, parallel with A3-A5)
- [ ] Drools integration (blocking calls on `Schedulers.boundedElastic()`)
- [ ] Rule authoring API (CRUD, versioning, rollback)
- [ ] Rule categories: adjudication, billing, eligibility, benefits, waiting periods
- [ ] Per-tenant rule storage, loading, and hot-reload
- [ ] Rule testing sandbox (dry-run)
- [ ] Default rule sets seeded for new tenants

---

## Stream B: Go Services (Weeks 4-12)

Can start as soon as foundation is done — no dependency on Java services for initial build.

### B1. API Gateway (Weeks 4-6)
- [ ] Fiber reverse proxy + route definitions
- [ ] JWT validation middleware (cookie-first, header-fallback)
- [ ] Tenant resolution middleware
- [ ] Rate limiting (per tenant, per user)
- [ ] Request logging (structured JSON), CORS, CSRF
- [ ] Health check aggregation
- [ ] Aggregated Swagger UI (merges all service OpenAPI specs)

### B2. Notification Service (Weeks 6-8)
- [ ] Kafka consumer for notification events
- [ ] Email (SES/Resend), SMS (Twilio/Africa's Talking), Push (FCM)
- [ ] Keycloak MFA SPI integration (SMS OTP dispatch)
- [ ] Per-tenant template management + rendering
- [ ] Delivery tracking + retry (exponential backoff)
- [ ] User notification preferences

### B3. Audit Service (Weeks 7-9)
- [ ] High-throughput Kafka consumer (batch processing)
- [ ] Append-only audit event storage (monthly partitions)
- [ ] Security event storage (Keycloak auth events + service access events)
- [ ] Query API (filter by tenant, user, action, entity, date range)
- [ ] Real-time security pattern detection (brute force, impossible travel, etc.)
- [ ] Compliance export (CSV/JSON)

### B4. Payment Gateway Service (Weeks 9-12)
- [ ] Provider abstraction layer (PaymentProvider interface)
- [ ] Paynow integration (EcoCash, OneMoney, cards, bank transfer — Zimbabwe)
- [ ] Stripe integration (cards, bank transfer — international)
- [ ] Webhook receiver + signature verification per provider
- [ ] Idempotency layer (duplicate payment prevention)
- [ ] Payment transaction ledger (public schema)
- [ ] Inbound: contribution payment initiation + confirmation
- [ ] Outbound: payout initiation (provider payments, member refunds)
- [ ] Recurring payment support (auto-debit contributions)
- [ ] Tenant self-service subscription: registration → payment → provisioning
- [ ] Subscription management (upgrade, downgrade, cancel, retry failed)
- [ ] Reconciliation job (poll stale transactions, flag discrepancies)
- [ ] All endpoints Swagger documented, audit-logged

### B5. File & Export Service (Weeks 10-12)
- [ ] S3 presigned URLs (upload/download)
- [ ] PDF generation (invoices, payment advice, statements, membership cards, receipts)
- [ ] CSV/XLSX export (financial reports, member lists, claim summaries)
- [ ] Bulk import (member CSV, tariff code imports)
- [ ] Virus scanning (ClamAV)

---

## Stream C: Client Applications (Weeks 5-20)

Starts once API Gateway + User Service are in progress. Uses mock APIs initially, switches to real backends as they come online.

### C1. Angular Web App (Weeks 5-18)

Build in priority order — each portal becomes usable as its backend service completes:

| Weeks | Portal | Backend Dependency |
|-------|--------|-------------------|
| 5-7 | Auth flow (Keycloak OIDC + PKCE, MFA), app shell, layout, routing | Keycloak + API Gateway |
| 7-9 | Super admin portal (tenant management, platform analytics, feature flags) | Tenancy Service |
| 8-10 | Tenant admin portal (settings, users, roles, rules config, branding, MFA policy) | User Service + Rules Engine |
| 10-14 | Claims portal (queues, adjudication workspace, AI recommendations, fraud flags) | Claims Service |
| 12-15 | Contributions portal (billing runs, schemes, member balances, invoicing) | Contributions Service |
| 13-16 | Finance portal (payment runs, reconciliation, reports, provider balances, forecasting) | Finance Service |
| 14-17 | Provider portal (multi-tenant dashboard, claim submission, payments, member verification) | Claims + Finance |
| 15-17 | Payment UI: contribution payments, payout execution, payment config, reconciliation | Payment Gateway |
| 16-18 | Audit log viewer, security events dashboard, tickets | Audit Service |
| 17-18 | Live dashboard integration (WebSocket channels) | Elixir Live Dashboard |

### C2. Flutter App (Weeks 8-20)

| Weeks | Feature | Backend Dependency |
|-------|---------|-------------------|
| 8-10 | Auth (Keycloak OIDC, MFA, biometric unlock), app shell, navigation | Keycloak |
| 10-13 | Member portal: dashboard, benefits, claims view, payments, bills | User + Claims + Contributions + Finance |
| 13-15 | Provider companion: claim submission (camera OCR), member verification (QR scan) | Claims + AI Service (OCR) |
| 15-17 | Chat (AI chatbot), push notifications (FCM), digital membership card | Chat Service + Notification Service |
| 16-18 | Bill payment (EcoCash, card, bank transfer), payment history, receipts, auto-pay | Payment Gateway |
| 17-19 | Offline mode, document upload/download | File Service |
| 19-20 | Polish, platform-specific fixes (iOS/Android), app store prep | — |

---

## Stream D: Elixir + Python Services (Weeks 9-18)

### D1. Live Dashboard — Elixir (Weeks 9-12)
- [ ] Phoenix Channels with JWT auth
- [ ] Channel topics: `dashboard:{tenant_id}`, `claims:{tenant_id}`, `finance:{tenant_id}`
- [ ] Kafka consumer → broadcast to channel subscribers
- [ ] Real-time claim status, financial metrics, presence tracking, notification feed

### D2. Chat Service — Elixir (Weeks 11-13)
- [ ] Phoenix Channels for member ↔ staff chat
- [ ] AI-assisted responses (proxy to AI Service)
- [ ] Chat history persistence, typing indicators, read receipts, file sharing

### D3. AI Service Foundation — Python (Weeks 10-13)
- [ ] FastAPI setup with async PostgreSQL + Kafka
- [ ] Claude API integration (Anthropic SDK)
- [ ] AI prediction storage schema (per-tenant)
- [ ] Feature engineering pipeline from claims/contributions data

### D4. Claims AI (Weeks 13-16)
- [ ] Auto-adjudication model (rule-based + ML + Claude reasoning hybrid)
- [ ] Fraud detection (Isolation Forest + XGBoost)
- [ ] Document OCR + data extraction (Tesseract + Claude Vision)
- [ ] Duplicate claim detection (fuzzy matching)
- [ ] Tariff code suggestion for providers

### D5. Finance & Analytics AI (Weeks 16-18)
- [ ] Billing optimization, financial forecasting
- [ ] Anomaly detection in transactions
- [ ] Member chatbot (Claude-powered FAQ + account queries)
- [ ] Provider intelligence (approval rates, peer benchmarking)

---

## Phase 5: Hardening & Launch (Weeks 19-22)

### Security (Weeks 19-20)
- [ ] Penetration testing (external firm)
- [ ] OWASP top 10 review across all services
- [ ] PHI/PII encryption audit (at rest + in transit)
- [ ] Keycloak hardening (brute force protection, session limits, token lifetimes)
- [ ] MFA enforcement verification across all roles
- [ ] Security event monitoring dashboard operational

### Performance (Weeks 19-20)
- [ ] Load testing (k6 or Gatling) — simulate concurrent tenants, users, claims
- [ ] Database query optimization (EXPLAIN ANALYZE on critical paths)
- [ ] Kafka consumer lag tuning, Redis cache optimization
- [ ] CDN configuration for Angular + Flutter web
- [ ] R2DBC connection pool tuning

### Staging Validation (Weeks 20-21)
- [ ] Full stack deployed to staging
- [ ] Test tenants with realistic data
- [ ] End-to-end workflow testing (claim → adjudication → payment)
- [ ] Multi-tenant isolation testing (verify no data leaks)
- [ ] MFA flow testing (all methods, all roles)
- [ ] Mobile app testing (iOS + Android, offline mode, push)

### Go-Live (Week 22)
- [ ] Production infrastructure via Terraform
- [ ] ArgoCD syncs production from main branch
- [ ] DNS (wildcard subdomain for tenants), TLS certificates
- [ ] First tenant onboarded
- [ ] Monitoring + alerting active
- [ ] On-call rotation established

---

## What Made the Compression Possible

| Before (38 weeks) | After (22 weeks) | How |
|-------------------|-------------------|-----|
| Sequential phases | 4 parallel streams | Go, Angular/Flutter, Elixir/Python all start before Java finishes |
| Foundation: 4 weeks | Foundation: 3 weeks | Tighter scope, defer non-critical infra |
| Clients start week 17 | Clients start week 5 | Mock APIs → switch to real backends as they come online |
| AI starts week 27 | AI starts week 10 | AI Service developed in parallel, integrates when Claims Service is ready |
| Elixir starts week 23 | Elixir starts week 9 | No dependency on Java services — consumes Kafka events that arrive later |
| Hardening: 6 weeks | Hardening: 4 weeks | Security + testing done continuously, final phase is validation not remediation |
| Go services wait for Java | Go services start week 4 | Gateway, notifications, audit are independent — they process Kafka events |

## Using the Legacy Codebase as Reference

The decommissioned legacy code in this repository serves as a domain knowledge reference:

| Legacy Project | Use It For |
|---------------|------------|
| `MASCA-Backend/claims/` | Understanding claim models, adjudication logic, tariff structures, verification flow |
| `MASCA-Backend/contributions/` | Understanding scheme models, billing cycles, balance tracking, transaction types |
| `MASCA-Backend/finance/` | Understanding payment models, payment runs, adjustments, provider balances |
| `MASCA-Backend/users/` | Understanding member/provider/group models, permission tiers, authentication flow |
| `Masca-Admin-Backend/` | Understanding audit event structure and what changes need tracking |
| `Email-Service/` | Understanding notification templates and email dispatch patterns |
| `masca-configurations/` | Understanding infrastructure requirements (what services need what resources) |
| React frontends | Understanding UI workflows, form fields, report layouts, dashboard components |

**Do NOT copy code from the legacy system.** It uses blocking Django/DRF patterns incompatible with the reactive WebFlux architecture. Use it only to understand the domain, then implement from scratch following the patterns in coding-standards.md.
