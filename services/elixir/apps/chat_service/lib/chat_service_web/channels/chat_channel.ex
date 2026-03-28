defmodule ChatServiceWeb.ChatChannel do
  use Phoenix.Channel
  alias ChatService.Chat
  alias ChatService.AiProxy

  @impl true
  def join("chat:" <> room_id, _payload, socket) do
    send(self(), {:after_join, room_id})
    {:ok, assign(socket, :room_id, room_id)}
  end

  @impl true
  def handle_info({:after_join, room_id}, socket) do
    # Load recent message history
    messages = Chat.list_messages(room_id, limit: 50)
    serialized = Enum.map(messages, fn msg ->
      %{
        id: msg.id,
        user_id: msg.user_id,
        body: msg.body,
        message_type: msg.message_type,
        metadata: msg.metadata,
        timestamp: DateTime.to_iso8601(msg.inserted_at)
      }
    end)

    push(socket, "chat:history", %{messages: serialized})
    push(socket, "chat:joined", %{room_id: room_id, user_id: socket.assigns.user_id})
    {:noreply, socket}
  end

  @impl true
  def handle_in("chat:message", %{"body" => body}, socket) do
    attrs = %{
      room_id: socket.assigns.room_id,
      user_id: socket.assigns.user_id,
      body: body,
      message_type: "text"
    }

    case Chat.create_message(attrs) do
      {:ok, msg} ->
        message = %{
          id: msg.id,
          room_id: socket.assigns.room_id,
          user_id: msg.user_id,
          body: msg.body,
          message_type: msg.message_type,
          timestamp: DateTime.to_iso8601(msg.inserted_at)
        }
        broadcast!(socket, "chat:new_message", message)
        {:reply, {:ok, message}, socket}
      {:error, _changeset} ->
        {:reply, {:error, %{reason: "failed to save message"}}, socket}
    end
  end

  @impl true
  def handle_in("chat:typing", _payload, socket) do
    broadcast_from!(socket, "chat:user_typing", %{user_id: socket.assigns.user_id})
    {:noreply, socket}
  end

  @impl true
  def handle_in("chat:read", %{"message_id" => message_id}, socket) do
    Chat.upsert_read_receipt(socket.assigns.room_id, socket.assigns.user_id, message_id)
    {:noreply, socket}
  end

  @impl true
  def handle_in("chat:ai_assist", %{"query" => query}, socket) do
    tenant_id = socket.assigns[:tenant_id] || "default"

    case AiProxy.get_ai_response(query, tenant_id, socket.assigns.user_id, socket.assigns.room_id) do
      {:ok, data} ->
        # Save AI response as a message
        Chat.create_message(%{
          room_id: socket.assigns.room_id,
          user_id: "ai-assistant",
          body: data["reply"],
          message_type: "ai_response"
        })

        push(socket, "chat:ai_response", %{
          body: data["reply"],
          source: data["source"],
          confidence: data["confidence"],
          timestamp: DateTime.utc_now() |> DateTime.to_iso8601()
        })

      {:error, reason} ->
        push(socket, "chat:ai_response", %{
          body: "AI assistant is temporarily unavailable. Please try again later.",
          source: "error",
          confidence: 0,
          error: reason,
          timestamp: DateTime.utc_now() |> DateTime.to_iso8601()
        })
    end

    {:noreply, socket}
  end

  @impl true
  def handle_in("chat:file", %{"file_url" => url, "filename" => name}, socket) do
    attrs = %{
      room_id: socket.assigns.room_id,
      user_id: socket.assigns.user_id,
      body: name,
      message_type: "file",
      metadata: %{"file_url" => url, "filename" => name}
    }

    case Chat.create_message(attrs) do
      {:ok, msg} ->
        message = %{
          id: msg.id,
          room_id: socket.assigns.room_id,
          user_id: msg.user_id,
          body: name,
          message_type: "file",
          metadata: msg.metadata,
          timestamp: DateTime.to_iso8601(msg.inserted_at)
        }
        broadcast!(socket, "chat:new_message", message)
        {:reply, {:ok, message}, socket}
      {:error, _} ->
        {:reply, {:error, %{reason: "failed to share file"}}, socket}
    end
  end
end
