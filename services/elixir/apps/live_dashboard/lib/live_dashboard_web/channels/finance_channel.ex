defmodule LiveDashboardWeb.FinanceChannel do
  use Phoenix.Channel

  @impl true
  def join("finance:" <> tenant_id, _payload, socket) do
    if socket.assigns.tenant_id == tenant_id do
      {:ok, socket}
    else
      {:error, %{reason: "unauthorized"}}
    end
  end

  @impl true
  def handle_in("payment:update", payload, socket) do
    broadcast!(socket, "payment:updated", payload)
    {:noreply, socket}
  end
end
