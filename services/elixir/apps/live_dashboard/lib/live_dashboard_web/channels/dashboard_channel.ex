defmodule LiveDashboardWeb.DashboardChannel do
  use Phoenix.Channel
  alias LiveDashboard.Stats.EventAggregator

  @impl true
  def join("dashboard:" <> tenant_id, _payload, socket) do
    if socket.assigns.tenant_id == tenant_id || tenant_id == "lobby" do
      send(self(), :after_join)
      # Subscribe to PubSub for real-time updates
      Phoenix.PubSub.subscribe(LiveDashboard.PubSub, "events:#{tenant_id}")
      {:ok, socket}
    else
      {:error, %{reason: "unauthorized"}}
    end
  end

  @impl true
  def handle_info(:after_join, socket) do
    tenant_id = socket.assigns.tenant_id
    stats = EventAggregator.get_stats(tenant_id)
    push(socket, "dashboard:snapshot", Map.put(stats, :timestamp, DateTime.utc_now() |> DateTime.to_iso8601()))
    {:noreply, socket}
  end

  @impl true
  def handle_info({:event, event_data}, socket) do
    push(socket, "dashboard:event", event_data)
    # Push updated stats
    stats = EventAggregator.get_stats(socket.assigns.tenant_id)
    push(socket, "dashboard:update", Map.put(stats, :timestamp, DateTime.utc_now() |> DateTime.to_iso8601()))
    {:noreply, socket}
  end

  @impl true
  def handle_in("request_update", _payload, socket) do
    stats = EventAggregator.get_stats(socket.assigns.tenant_id)
    push(socket, "dashboard:update", Map.put(stats, :timestamp, DateTime.utc_now() |> DateTime.to_iso8601()))
    {:noreply, socket}
  end
end
