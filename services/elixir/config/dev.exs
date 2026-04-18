import Config

config :live_dashboard, LiveDashboardWeb.Endpoint,
  http: [ip: {0, 0, 0, 0}, port: 4000],
  adapter: Bandit.PhoenixAdapter,
  debug_errors: true,
  code_reloader: true

config :chat_service, ChatServiceWeb.Endpoint,
  http: [ip: {0, 0, 0, 0}, port: 4001],
  adapter: Bandit.PhoenixAdapter,
  debug_errors: true,
  code_reloader: true

# Disable OTLP export in local dev
config :opentelemetry, traces_exporter: :none
