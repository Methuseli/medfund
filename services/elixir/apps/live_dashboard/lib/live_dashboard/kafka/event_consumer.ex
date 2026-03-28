defmodule LiveDashboard.Kafka.EventConsumer do
  @moduledoc """
  Broadway Kafka consumer for real-time dashboard events.
  Consumes from claims, finance, and user event topics.
  """
  use Broadway
  require Logger

  alias LiveDashboard.Stats.EventAggregator

  def start_link(opts) do
    kafka_brokers = Keyword.get(opts, :brokers, [localhost: 9092])
    group_id = Keyword.get(opts, :group_id, "live-dashboard-consumer")

    Broadway.start_link(__MODULE__,
      name: __MODULE__,
      producer: [
        module: {
          BroadwayKafka.Producer,
          [
            hosts: kafka_brokers,
            group_id: group_id,
            topics: [
              "medfund.claims.submitted",
              "medfund.claims.adjudicated",
              "medfund.finance.payment-created",
              "medfund.users.member-enrolled"
            ]
          ]
        },
        concurrency: 1
      ],
      processors: [
        default: [concurrency: 2]
      ],
      batchers: [
        default: [batch_size: 10, batch_timeout: 2_000]
      ]
    )
  end

  @impl true
  def handle_message(_, message, _) do
    case Jason.decode(message.data) do
      {:ok, event} ->
        process_event(event)
      {:error, reason} ->
        Logger.warning("Failed to decode Kafka message: #{inspect(reason)}")
    end

    message
  end

  @impl true
  def handle_batch(_, messages, _, _) do
    messages
  end

  defp process_event(event) do
    event_type = Map.get(event, "event", "")
    tenant_id = Map.get(event, "tenantId", "unknown")

    Logger.debug("Processing event: #{event_type} for tenant #{tenant_id}")

    # Update aggregator stats
    EventAggregator.record_event(event_type, tenant_id)

    # Broadcast to PubSub for connected WebSocket clients
    Phoenix.PubSub.broadcast(
      LiveDashboard.PubSub,
      "events:#{tenant_id}",
      {:event, %{event_type: event_type, data: event, timestamp: DateTime.utc_now() |> DateTime.to_iso8601()}}
    )
  end
end
