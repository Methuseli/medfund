defmodule ChatServiceWeb.RoomController do
  use Phoenix.Controller, formats: [:json]
  alias ChatService.Chat

  def index(conn, _params) do
    tenant_id = get_req_header(conn, "x-tenant-id") |> List.first() || "default"
    rooms = Chat.list_rooms(tenant_id)
    json(conn, %{rooms: Enum.map(rooms, &room_to_map/1)})
  end

  def messages(conn, %{"room_id" => room_id}) do
    messages = Chat.list_messages(room_id, limit: 50)
    json(conn, %{
      room_id: room_id,
      messages: Enum.map(messages, &message_to_map/1)
    })
  end

  defp room_to_map(room) do
    %{
      id: room.id,
      tenant_id: room.tenant_id,
      name: room.name,
      room_type: room.room_type,
      participants: room.participants,
      created_at: DateTime.to_iso8601(room.inserted_at)
    }
  end

  defp message_to_map(msg) do
    %{
      id: msg.id,
      user_id: msg.user_id,
      body: msg.body,
      message_type: msg.message_type,
      metadata: msg.metadata,
      timestamp: DateTime.to_iso8601(msg.inserted_at)
    }
  end
end
