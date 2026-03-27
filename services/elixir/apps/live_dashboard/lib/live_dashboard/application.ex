defmodule LiveDashboard.Application do
  use Application

  @impl true
  def start(_type, _args) do
    children = [
      LiveDashboard.Repo,
      LiveDashboardWeb.Endpoint
    ]

    opts = [strategy: :one_for_one, name: LiveDashboard.Supervisor]
    Supervisor.start_link(children, opts)
  end
end
