defmodule LiveDashboardWeb.Router do
  use Phoenix.Router
  import Plug.Conn

  pipeline :api do
    plug :accepts, ["json"]
  end

  scope "/api/v1/dashboard", LiveDashboardWeb do
    pipe_through :api
    get "/health", HealthController, :index
  end
end
