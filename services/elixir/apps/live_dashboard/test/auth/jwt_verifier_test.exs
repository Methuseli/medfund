defmodule LiveDashboard.Auth.JwtVerifierTest do
  use ExUnit.Case, async: true

  alias LiveDashboard.Auth.JwtVerifier

  test "verify_token with valid JWT format returns claims" do
    # Create a minimal JWT (header.payload.signature)
    payload = %{"sub" => "user-123", "tenant_id" => "tenant-abc"} |> Jason.encode!() |> Base.url_encode64(padding: false)
    header = %{"alg" => "RS256"} |> Jason.encode!() |> Base.url_encode64(padding: false)
    token = "#{header}.#{payload}.fake-signature"

    assert {:ok, claims} = JwtVerifier.verify_token(token)
    assert claims["sub"] == "user-123"
    assert claims["tenant_id"] == "tenant-abc"
  end

  test "verify_token with invalid format returns error" do
    assert {:error, _} = JwtVerifier.verify_token("not-a-jwt")
  end

  test "verify_token with nil returns error" do
    assert {:error, _} = JwtVerifier.verify_token(nil)
  end

  test "verify_token extracts default tenant_id" do
    payload = %{"sub" => "user-1"} |> Jason.encode!() |> Base.url_encode64(padding: false)
    header = %{"alg" => "RS256"} |> Jason.encode!() |> Base.url_encode64(padding: false)
    token = "#{header}.#{payload}.sig"

    assert {:ok, claims} = JwtVerifier.verify_token(token)
    assert claims["tenant_id"] == "default"
  end
end
