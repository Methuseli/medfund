defmodule LiveDashboardWeb.HealthController do
  use Phoenix.Controller, formats: [:json]

  def index(conn, _params) do
    json(conn, %{status: "ok", service: "live-dashboard"})
  end
end
