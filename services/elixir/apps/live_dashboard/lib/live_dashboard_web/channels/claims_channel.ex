defmodule LiveDashboardWeb.ClaimsChannel do
  use Phoenix.Channel

  @impl true
  def join("claims:" <> tenant_id, _payload, socket) do
    if socket.assigns.tenant_id == tenant_id do
      {:ok, socket}
    else
      {:error, %{reason: "unauthorized"}}
    end
  end

  @impl true
  def handle_in("claim:status_update", %{"claim_id" => claim_id, "status" => status}, socket) do
    broadcast!(socket, "claim:updated", %{claim_id: claim_id, status: status})
    {:noreply, socket}
  end
end
