defmodule ChatService.AiProxy do
  @moduledoc "HTTP proxy to the Python AI Service for AI-assisted chat responses."
  require Logger

  @ai_service_url Application.compile_env(:chat_service, :ai_service_url, "http://localhost:8000")

  def get_ai_response(query, tenant_id, user_id, room_id) do
    url = "#{@ai_service_url}/api/v1/ai/chat/message"

    body = Jason.encode!(%{
      message: query,
      conversation_id: room_id,
      context: %{tenant_id: tenant_id, user_id: user_id}
    })

    headers = [
      {"content-type", "application/json"},
      {"x-tenant-id", tenant_id}
    ]

    case http_post(url, body, headers) do
      {:ok, %{status_code: 200, body: resp_body}} ->
        case Jason.decode(resp_body) do
          {:ok, data} -> {:ok, data}
          _ -> {:error, "Failed to parse AI response"}
        end
      {:ok, %{status_code: status}} ->
        Logger.warning("AI service returned #{status}")
        {:error, "AI service error: #{status}"}
      {:error, reason} ->
        Logger.warning("AI proxy failed: #{inspect(reason)}")
        {:error, "AI service unavailable"}
    end
  end

  defp http_post(url, body, headers) do
    # Use :httpc from Erlang stdlib (no extra deps needed)
    url_charlist = String.to_charlist(url)
    body_charlist = String.to_charlist(body)
    headers_charlist = Enum.map(headers, fn {k, v} -> {String.to_charlist(k), String.to_charlist(v)} end)

    case :httpc.request(:post, {url_charlist, headers_charlist, ~c"application/json", body_charlist}, [], []) do
      {:ok, {{_, status_code, _}, _resp_headers, resp_body}} ->
        {:ok, %{status_code: status_code, body: List.to_string(resp_body)}}
      {:error, reason} ->
        {:error, reason}
    end
  end
end
