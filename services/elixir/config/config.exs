import Config

config :live_dashboard, LiveDashboard.Repo,
  username: "medfund",
  password: "medfund",
  hostname: "172.29.83.165",
  database: "medfund",
  port: 5432

config :live_dashboard, LiveDashboardWeb.Endpoint,
  url: [host: "localhost"],
  secret_key_base: "CHANGE_ME_IN_PRODUCTION"

config :chat_service, ChatService.Repo,
  username: "medfund",
  password: "medfund",
  hostname: "172.29.83.165",
  database: "medfund",
  port: 5432

config :chat_service, ChatServiceWeb.Endpoint,
  url: [host: "localhost"],
  secret_key_base: "CHANGE_ME_IN_PRODUCTION"

config :logger, :console,
  format: "$time $metadata[$level] $message\n",
  metadata: [:request_id, :tenant_id]

import_config "#{config_env()}.exs"
