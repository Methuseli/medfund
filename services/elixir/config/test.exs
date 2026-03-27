import Config

config :live_dashboard, LiveDashboard.Repo,
  pool: Ecto.Adapters.SQL.Sandbox

config :chat_service, ChatService.Repo,
  pool: Ecto.Adapters.SQL.Sandbox
