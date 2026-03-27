# MedFund Platform — Architecture Guidelines

This document is the root guide for Claude Code when working on the MedFund healthcare claims platform. The legacy system has been decommissioned — this is a **greenfield build**. The legacy codebase in this repo serves only as domain knowledge reference.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Core Backend** | Java 21 + Spring Boot 3.3 WebFlux (claims, contributions, finance, tenancy, rules engine) |
| **High-Throughput Services** | Go 1.23 + Fiber v2 (API gateway, notifications, file processing, audit ingestion) |
| **Real-Time** | Elixir 1.17 + Phoenix 1.7 (live dashboards, WebSocket channels, chat) |
| **AI/ML Service** | Python 3.12 + FastAPI (adjudication AI, fraud detection, OCR, chatbot, analytics) |
| **Web Frontend** | Angular 19 (single app, role-based routing for super admin, tenant admin, operations, providers) |
| **Mobile App** | Flutter 3.x (iOS + Android + Web PWA — member portal + provider companion) |
| **Database** | Schema-per-tenant PostgreSQL 17 |
| **Message Broker** | Apache Kafka (event backbone) + Redis (job queues) |
| **Auth** | Keycloak (OIDC/OAuth2, per-tenant realms, MFA: TOTP + Email OTP + SMS OTP) |
| **Infrastructure** | Helm 3 + ArgoCD + GitHub Actions + Terraform (AWS) |
| **Observability** | OpenTelemetry + Grafana stack (Loki, Tempo, Mimir) |

## Architecture Documents

Read these before starting any implementation work:

1. **[Architecture Overview](architecture.md)** — System design, service boundaries, data flow, polyglot strategy
2. **[Tech Stack](tech-stack.md)** — Exact versions, libraries, and rationale for every technology choice
3. **[Build Strategy](migration-strategy.md)** — Phased greenfield build plan (4 parallel streams, ~22 weeks)
4. **[AI Integration](ai-integration.md)** — Where and how AI is used across the platform
5. **[Multi-Currency](multi-currency.md)** — Currency handling, exchange rates, financial precision
6. **[Multi-Tenancy](multi-tenancy.md)** — Schema-per-tenant design, tenant resolution, data isolation, per-tenant rules
7. **[Claims Adjudication](adjudication.md)** — Six-stage pipeline, tariffs, ICD-10, AHFOZ, modifiers, rules engine + AI
8. **[Rules Engine](rules-engine.md)** — Visual rule builder, rule categories, templates, testing sandbox, hot-reload
9. **[Payment Gateway](payments.md)** — Online payments (contributions, subscriptions, payouts), provider integrations
10. **[Infrastructure & DevOps](infrastructure.md)** — Deployment, CI/CD, observability, security
11. **[Coding Standards](coding-standards.md)** — Per-language conventions, testing, error handling
12. **[Portals & Roles](portals.md)** — Super admin, tenant admin, provider, member, group liaison portal specifications

## Critical Rules

1. **Never mix currencies in arithmetic.** Always convert to a common currency using the exchange rate service before comparing or summing amounts. Use `BigDecimal` (Java) / `decimal` (others) — never floating point for money.
2. **Every database query must be tenant-scoped.** Java services use the tenant-aware `TenantContext` interceptor. Every service must resolve tenant from JWT or subdomain.
3. **AI decisions must be auditable.** Every AI-assisted adjudication, fraud flag, or billing suggestion must log the model version, input features, confidence score, and output — with a human-reviewable trail.
4. **Healthcare compliance first.** PHI/PII must be encrypted at rest and in transit. Audit logs are immutable. Data retention policies apply per tenant jurisdiction.
   - **MFA is mandatory** for all admin and staff roles. Supports TOTP (authenticator apps), Email OTP, and SMS OTP — all via Keycloak. Tenant admins configure which methods and roles require MFA.
   - **OAuth 2.0 / OIDC** via Keycloak for all auth flows. Authorization Code + PKCE for web and mobile clients. No custom auth logic — Keycloak is the single identity provider. Social login (Google, Microsoft, Apple, SAML) configurable per tenant.
5. **Per-tenant rules live in Java.** Business rules that vary by tenant (adjudication rules, contribution schedules, waiting periods, benefit limits) are managed in the Spring Boot rules engine with Drools or a custom DSL. Tenant admins configure these via their admin portal.
6. **Service communication via Kafka events.** Services do not call each other directly for side effects. Use Kafka topics for async event-driven communication. Synchronous calls only for query/read operations via gRPC or REST.
7. **Every API endpoint must be documented in Swagger (OpenAPI 3.1).** No endpoint ships without a complete Swagger definition — request/response schemas, status codes, descriptions, example payloads, and authentication requirements. Swagger UI must be accessible at `/swagger-ui` (Java), `/docs` (Python), or equivalent for each service.
8. **Every entity mutation must be audit-logged.** Every CREATE, UPDATE, DELETE on business entities must emit an audit event to Kafka with actor, old value, new value, changed fields, and correlation ID. Audit events are immutable (append-only, no updates or deletes). See coding-standards.md for implementation per language.
9. **All security events must be logged.** Login, logout, failed auth, MFA, password changes, role assignments, permission denials, impersonation — all go to the security events table. Keycloak event listener pushes auth events; services emit access events. Real-time alerting on suspicious patterns (brute force, impossible travel, privilege escalation).
