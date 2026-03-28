defmodule LiveDashboardWeb.DashboardChannel do
  use Phoenix.Channel

  @impl true
  def join("dashboard:" <> tenant_id, _payload, socket) do
    if socket.assigns.tenant_id == tenant_id || tenant_id == "lobby" do
      send(self(), :after_join)
      {:ok, socket}
    else
      {:error, %{reason: "unauthorized"}}
    end
  end

  @impl true
  def handle_info(:after_join, socket) do
    push(socket, "dashboard:snapshot", %{
      claims_today: 0,
      claims_pending: 0,
      payments_today: 0,
      members_active: 0,
      timestamp: DateTime.utc_now() |> DateTime.to_iso8601()
    })
    {:noreply, socket}
  end

  @impl true
  def handle_in("request_update", _payload, socket) do
    push(socket, "dashboard:update", %{
      claims_today: 0,
      claims_pending: 0,
      timestamp: DateTime.utc_now() |> DateTime.to_iso8601()
    })
    {:noreply, socket}
  end
end
