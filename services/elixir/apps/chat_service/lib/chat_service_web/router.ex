defmodule ChatServiceWeb.Router do
  use Phoenix.Router
  import Plug.Conn

  pipeline :api do
    plug :accepts, ["json"]
  end

  scope "/api/v1/chat", ChatServiceWeb do
    pipe_through :api
    get "/health", HealthController, :index
    get "/rooms", RoomController, :index
    get "/rooms/:room_id/messages", RoomController, :messages
  end
end
