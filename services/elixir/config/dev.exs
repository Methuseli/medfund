import Config

config :live_dashboard, LiveDashboardWeb.Endpoint,
  http: [port: 4000],
  debug_errors: true,
  code_reloader: true

config :chat_service, ChatServiceWeb.Endpoint,
  http: [port: 4001],
  debug_errors: true,
  code_reloader: true
