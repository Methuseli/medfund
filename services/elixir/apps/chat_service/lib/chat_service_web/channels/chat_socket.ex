defmodule ChatServiceWeb.ChatSocket do
  use Phoenix.Socket

  channel "chat:*", ChatServiceWeb.ChatChannel

  @impl true
  def connect(%{"token" => token}, socket, _connect_info) do
    # Reuse same JWT decode logic as dashboard (simplified for umbrella)
    case decode_token(token) do
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

  defp decode_token(token) do
    case String.split(token, ".") do
      [_header, payload, _signature] ->
        with {:ok, json} <- Base.url_decode64(payload, padding: false),
             {:ok, claims} <- Jason.decode(json) do
          {:ok, %{
            "sub" => Map.get(claims, "sub", "unknown"),
            "tenant_id" => Map.get(claims, "tenant_id", Map.get(claims, "tenantId", "default"))
          }}
        else
          _ -> {:error, :invalid_token}
        end
      _ -> {:error, :invalid_token}
    end
  end
end
