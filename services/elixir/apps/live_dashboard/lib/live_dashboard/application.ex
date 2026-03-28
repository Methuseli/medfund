defmodule LiveDashboard.Application do
  use Application

  @impl true
  def start(_type, _args) do
    children = [
      LiveDashboard.Repo,
      {Phoenix.PubSub, name: LiveDashboard.PubSub},
      LiveDashboard.Stats.EventAggregator,
      LiveDashboardWeb.Endpoint
    ]

    # Add Kafka consumer only if configured and not in test
    children = if start_kafka?() do
      brokers = Application.get_env(:live_dashboard, :kafka_brokers, [localhost: 9092])
      children ++ [{LiveDashboard.Kafka.EventConsumer, [brokers: brokers]}]
    else
      children
    end

    opts = [strategy: :one_for_one, name: LiveDashboard.Supervisor]
    Supervisor.start_link(children, opts)
  end

  defp start_kafka? do
    Application.get_env(:live_dashboard, :start_kafka, false) && Mix.env() != :test
  end
end
