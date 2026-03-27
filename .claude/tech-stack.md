# Tech Stack

Every technology choice includes the rationale. Do not substitute alternatives without updating this document.

---

## Java Services (Claims, Contributions, Finance, Tenancy, Rules, Users)

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| **Language** | Java | 21 (LTS) | Virtual threads, pattern matching, records, sealed classes |
| **Framework** | Spring Boot (WebFlux) | 3.3.x | Reactive, non-blocking I/O via Project Reactor. Use `spring-boot-starter-webflux` instead of `spring-boot-starter-web` |
| **Reactive Stack** | Spring WebFlux + Project Reactor | 3.3.x / 3.6.x | `Mono<T>` and `Flux<T>` for async pipelines. Netty as the default embedded server (not Tomcat) |
| **Build** | Gradle (Kotlin DSL) | 8.x | Faster builds, better dependency management than Maven |
| **ORM** | Spring Data R2DBC + Hibernate Reactive | latest / 2.x | R2DBC for non-blocking database access. Hibernate Reactive for complex queries. Schema-per-tenant via connection-level `SET search_path` |
| **R2DBC Driver** | r2dbc-postgresql (io.r2dbc) | latest | Reactive PostgreSQL driver. Connection pooling via `r2dbc-pool` |
| **DB Migrations** | Flyway | 10.x | Per-tenant schema migrations, version-controlled, repeatable |
| **Validation** | Jakarta Validation (Hibernate Validator) | 3.x | Declarative bean validation, custom constraint annotations |
| **API Docs** | SpringDoc OpenAPI | 2.x | Auto-generate OpenAPI 3.1 from Spring controllers |
| **Auth** | Spring Security OAuth2 Resource Server | 6.x | Reactive OIDC/OAuth2 resource server, validates Keycloak JWTs, tenant-aware security context |
| **Rules Engine** | Drools | 9.x | Per-tenant business rules, hot-reload, rule authoring API |
| **Messaging** | reactor-kafka | 1.3.x | Reactive Kafka producer/consumer, backpressure-aware, integrates with Project Reactor pipelines |
| **Cache** | Spring Data Redis Reactive (Lettuce) | latest | Non-blocking Redis access, tenant-scoped caching, distributed cache invalidation |
| **gRPC** | grpc-spring-boot-starter | latest | Inter-service synchronous queries |
| **Money** | JavaMoney (JSR 354) / Moneta | 1.4 | Industry-standard monetary calculations, currency conversion |
| **Testing** | JUnit 5 + Testcontainers + WebTestClient | latest | `WebTestClient` for reactive endpoint testing. Testcontainers for real PostgreSQL, Kafka, Redis |
| **Linting** | Checkstyle + SpotBugs | latest | Code quality enforcement |

## Go Services (API Gateway, Notifications, Audit, File/Export)

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| **Language** | Go | 1.23+ | Raw performance, low memory footprint, excellent concurrency |
| **Web Framework** | Fiber | v2 | Express-inspired, fastest Go framework, low-allocation design |
| **Routing** | Fiber built-in | — | Path-based routing with middleware chain |
| **Kafka** | confluent-kafka-go | latest | High-performance Kafka client (librdkafka-based) |
| **Redis** | go-redis/redis | v9 | Connection pooling, pipeline support |
| **gRPC** | google.golang.org/grpc | latest | Service-to-service sync calls |
| **Database** | pgx | v5 | Native PostgreSQL driver, connection pooling, zero-allocation scanning |
| **S3** | aws-sdk-go-v2 | latest | Presigned URLs, multipart upload |
| **PDF** | go-pdf/fpdf or wkhtmltopdf | latest | Invoice/statement generation |
| **CSV/XLSX** | excelize | v2 | Financial report exports |
| **Payment Providers** | Paynow SDK, Stripe SDK, Paystack SDK | latest | Payment gateway integrations (inbound + outbound) |
| **Email** | AWS SES SDK / Resend | latest | Transactional email delivery |
| **SMS** | Twilio / Africa's Talking SDK | latest | SMS notifications |
| **Push** | Firebase Admin SDK | latest | FCM push notifications for Flutter mobile |
| **Testing** | Go testing + testify | latest | Table-driven tests, mocks |
| **Linting** | golangci-lint | latest | Comprehensive linter aggregator |

## Elixir Services (Live Dashboard, Chat)

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| **Language** | Elixir | 1.17+ | BEAM VM: massive concurrency, fault tolerance, soft real-time |
| **Framework** | Phoenix | 1.7.x | LiveView for real-time dashboards, Channels for WebSocket |
| **Real-Time** | Phoenix Channels | built-in | WebSocket multiplexing, presence tracking, topic-based pub/sub |
| **LiveView** | Phoenix LiveView | 1.0 | Server-rendered real-time UI for dashboard widgets |
| **Database** | Ecto | 3.x | PostgreSQL adapter, migrations, changesets |
| **Kafka** | Broadway + BroadwayKafka | latest | Concurrent Kafka consumer with back-pressure |
| **Cache** | Cachex | latest | In-process cache with TTL |
| **Testing** | ExUnit + Wallaby | built-in | Unit + browser-based E2E tests |

## Python AI Service

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| **Language** | Python | 3.12+ | ML ecosystem, Claude SDK, data science libraries |
| **Framework** | FastAPI | 0.115+ | Async-native, auto OpenAPI docs, Pydantic validation |
| **AI/LLM** | Anthropic SDK (Claude) | latest | Claims adjudication reasoning, document analysis, chatbot |
| **ML** | scikit-learn + XGBoost | latest | Fraud detection, claims classification, anomaly detection |
| **Deep Learning** | PyTorch | 2.x | Complex models when needed (NLP, document understanding) |
| **OCR** | Tesseract + pdf2image | latest | Claim document digitization and data extraction |
| **Data** | pandas + numpy | latest | Data processing, feature engineering |
| **Database** | SQLAlchemy 2 (async) | 2.0+ | Read-only access to tenant data for ML features |
| **Kafka** | aiokafka | latest | Async Kafka consumer/producer |
| **Validation** | Pydantic v2 | 2.x | Request/response schemas, strict mode |
| **Task Queue** | Celery + Redis | 5.x | Long-running ML training jobs, batch predictions |
| **Testing** | pytest + httpx | latest | Async test support |
| **Linting** | Ruff | latest | Fast Python linter + formatter |
| **Dependency Mgmt** | uv | latest | Fast Python package management |

## Angular Web Application (Admin/Operations)

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| **Framework** | Angular | 19.x | Enterprise-grade, opinionated, excellent for large admin apps |
| **Language** | TypeScript | 5.7+ | Strict mode, Angular requires it |
| **UI Components** | Angular Material + PrimeNG | latest | Material for system UI, PrimeNG for data-heavy components (tables, charts) |
| **Styling** | Tailwind CSS | 4.x | Utility-first, design tokens via CSS variables |
| **State** | NgRx (Signal Store) | 18.x | Reactive state management, Angular Signals integration |
| **Forms** | Angular Reactive Forms | built-in | Type-safe forms with validators |
| **Tables** | PrimeNG Table | latest | Virtual scrolling, filtering, sorting, export — ideal for finance/claims grids |
| **Charts** | ngx-charts or PrimeNG Charts | latest | Dashboard visualizations |
| **HTTP** | Angular HttpClient | built-in | Interceptors for auth, tenant headers, error handling |
| **Auth** | angular-oauth2-oidc | latest | Keycloak OIDC with Authorization Code + PKCE, silent refresh, MFA support |
| **i18n** | @angular/localize | built-in | Multi-language support per tenant locale |
| **PDF** | pdfmake or jsPDF | latest | Client-side PDF preview/export |
| **Testing** | Karma + Jasmine + Cypress | built-in + latest | Unit + E2E tests |
| **Linting** | ESLint (angular-eslint) | latest | Angular-specific linting rules |

## Flutter Application (Member-Facing)

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| **Framework** | Flutter | 3.x | Single codebase: iOS, Android, Web (PWA) |
| **Language** | Dart | 3.x | Sound null safety, strong typing |
| **State** | Riverpod | 2.x | Compile-safe, testable, provider-based state management |
| **Navigation** | go_router | latest | Declarative routing, deep linking, guards |
| **HTTP** | Dio | latest | Interceptors, retry, cancel tokens |
| **Auth** | flutter_appauth | latest | Keycloak OIDC with Authorization Code + PKCE via system browser, MFA support |
| **Local Storage** | Hive or Isar | latest | Offline caching of member data |
| **Push Notif.** | firebase_messaging | latest | FCM integration |
| **Charts** | fl_chart | latest | Benefit usage, claim history visualizations |
| **PDF Viewer** | flutter_pdfview | latest | View payment advice, invoices |
| **QR Code** | qr_flutter | latest | Digital membership card |
| **Biometrics** | local_auth | latest | Fingerprint/face unlock |
| **Testing** | flutter_test + integration_test | built-in | Widget + integration tests |

## Shared Infrastructure

| Technology | Version | Purpose |
|-----------|---------|---------|
| **PostgreSQL** | 17 | Primary database, schema-per-tenant, partitioned audit tables |
| **Redis** | 7.x | Caching, session store, job queues (BullMQ equivalent), rate limiting |
| **Apache Kafka** | 3.8+ | Event backbone, cross-service communication, audit event streaming |
| **Confluent Schema Registry** | latest | Avro schema validation for Kafka events |
| **Keycloak** | 26.x | Identity and access management, per-tenant realms, MFA (TOTP + Email OTP + SMS OTP), OAuth 2.0/OIDC, social login (Google, Microsoft, Apple, SAML federation) |
| **MinIO / AWS S3** | latest | Object storage (documents, exports, media) |
| **Docker** | latest | Container images (multi-stage builds per language) |
| **Kubernetes** | 1.30+ | Container orchestration (EKS or managed K8s) |
| **Helm** | 3.x | K8s package management |
| **ArgoCD** | 2.x | GitOps continuous delivery |
| **GitHub Actions** | — | CI pipeline (build, test, lint, security scan, container push) |
| **Terraform** | 1.x | Infrastructure as Code for cloud resources |
| **OpenTelemetry** | latest | Distributed tracing, metrics, logs (all languages have OTel SDKs) |
| **Grafana** | latest | Dashboards + Loki (logs) + Tempo (traces) + Mimir (metrics) |
| **Trivy** | latest | Container image vulnerability scanning |
| **SOPS + age** | latest | Secrets encryption in Git |
| **Protobuf** | 3.x | gRPC service contracts, shared across Java/Go/Elixir/Python |
