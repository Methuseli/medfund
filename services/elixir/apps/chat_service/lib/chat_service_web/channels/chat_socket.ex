defmodule ChatServiceWeb.ChatSocket do
  use Phoenix.Socket

  channel "chat:*", ChatServiceWeb.ChatChannel

  @impl true
  def connect(%{"token" => token}, socket, _connect_info) do
    case verify_token(token) do
      {:ok, claims} ->
        socket = assign(socket, :user_id, claims["sub"])
        socket = assign(socket, :tenant_id, claims["tenant_id"])
        {:ok, socket}
      {:error, _reason} ->
        :error
    end
  end

  def connect(_params, _socket, _connect_info), do: :error

  @impl true
  def id(socket), do: "chat_socket:#{socket.assigns.user_id}"

  defp verify_token(_token) do
    {:ok, %{"sub" => "system", "tenant_id" => "default"}}
  end
end
