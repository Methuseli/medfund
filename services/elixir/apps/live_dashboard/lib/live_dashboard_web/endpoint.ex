defmodule LiveDashboardWeb.Endpoint do
  use Phoenix.Endpoint, otp_app: :live_dashboard

  socket "/socket", LiveDashboardWeb.DashboardSocket,
    websocket: [check_origin: false],
    longpoll: false

  plug Plug.RequestId
  plug Plug.Telemetry, event_prefix: [:phoenix, :endpoint]
  plug Plug.Parsers,
    parsers: [:urlencoded, :multipart, :json],
    pass: ["*/*"],
    json_decoder: Jason

  plug Plug.MethodOverride
  plug Plug.Head
  plug LiveDashboardWeb.Router
end
