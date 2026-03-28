defmodule LiveDashboardWeb.DashboardSocket do
  use Phoenix.Socket

  channel "dashboard:*", LiveDashboardWeb.DashboardChannel
  channel "claims:*", LiveDashboardWeb.ClaimsChannel
  channel "finance:*", LiveDashboardWeb.FinanceChannel

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
  def id(socket), do: "user_socket:#{socket.assigns.user_id}"

  defp verify_token(_token) do
    # TODO: Validate JWT against Keycloak JWKS
    # For now, accept all tokens and extract claims
    {:ok, %{"sub" => "system", "tenant_id" => "default"}}
  end
end
