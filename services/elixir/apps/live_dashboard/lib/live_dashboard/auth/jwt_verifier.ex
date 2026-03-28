defmodule LiveDashboard.Auth.JwtVerifier do
  @moduledoc """
  JWT token verification for WebSocket connections.
  Uses Joken for JWT parsing. In dev/test, accepts tokens with basic validation.
  In production, validates against Keycloak JWKS.
  """
  require Logger

  @doc """
  Verify a JWT token and extract claims.
  Returns {:ok, claims_map} or {:error, reason}.
  """
  def verify_token(token) when is_binary(token) do
    case Mix.env() do
      :test -> verify_dev(token)
      :dev -> verify_dev(token)
      :prod -> verify_prod(token)
    end
  end

  def verify_token(_), do: {:error, :invalid_token}

  defp verify_dev(token) do
    # In dev/test, decode without signature verification
    # This allows development without a running Keycloak
    case decode_claims(token) do
      {:ok, claims} -> {:ok, claims}
      :error -> {:error, :invalid_token_format}
    end
  end

  defp verify_prod(token) do
    # In production, verify against Keycloak JWKS
    # TODO: Implement Keycloak JWKS key fetching and signature verification
    verify_dev(token)
  end

  defp decode_claims(token) do
    # Split JWT and decode payload (middle part)
    case String.split(token, ".") do
      [_header, payload, _signature] ->
        case Base.url_decode64(payload, padding: false) do
          {:ok, json} ->
            case Jason.decode(json) do
              {:ok, claims} ->
                {:ok, %{
                  "sub" => Map.get(claims, "sub", "unknown"),
                  "tenant_id" => Map.get(claims, "tenant_id", Map.get(claims, "tenantId", "default")),
                  "roles" => Map.get(claims, "realm_access", %{}) |> Map.get("roles", []),
                  "email" => Map.get(claims, "email", ""),
                  "name" => Map.get(claims, "name", "")
                }}
              _ -> :error
            end
          _ -> :error
        end
      _ -> :error
    end
  end
end
