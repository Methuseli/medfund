defmodule ChatServiceWeb.ChatChannel do
  use Phoenix.Channel

  @impl true
  def join("chat:" <> room_id, _payload, socket) do
    send(self(), {:after_join, room_id})
    {:ok, assign(socket, :room_id, room_id)}
  end

  @impl true
  def handle_info({:after_join, room_id}, socket) do
    push(socket, "chat:joined", %{room_id: room_id, user_id: socket.assigns.user_id})
    {:noreply, socket}
  end

  @impl true
  def handle_in("chat:message", %{"body" => body}, socket) do
    message = %{
      id: Ecto.UUID.generate(),
      room_id: socket.assigns.room_id,
      user_id: socket.assigns.user_id,
      body: body,
      timestamp: DateTime.utc_now() |> DateTime.to_iso8601()
    }

    broadcast!(socket, "chat:new_message", message)
    {:reply, {:ok, message}, socket}
  end

  @impl true
  def handle_in("chat:typing", _payload, socket) do
    broadcast_from!(socket, "chat:user_typing", %{user_id: socket.assigns.user_id})
    {:noreply, socket}
  end

  @impl true
  def handle_in("chat:ai_assist", %{"query" => query}, socket) do
    # TODO: Proxy to AI Service for AI-assisted responses
    response = %{
      source: "ai",
      body: "AI assistance is not yet connected. Query: #{query}",
      timestamp: DateTime.utc_now() |> DateTime.to_iso8601()
    }
    push(socket, "chat:ai_response", response)
    {:noreply, socket}
  end
end
