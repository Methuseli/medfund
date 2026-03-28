defmodule ChatServiceWeb.RoomController do
  use Phoenix.Controller, formats: [:json]

  def index(conn, _params) do
    # Stub: return empty room list
    json(conn, %{rooms: []})
  end

  def messages(conn, %{"room_id" => room_id}) do
    # Stub: return empty message list
    json(conn, %{room_id: room_id, messages: []})
  end
end
