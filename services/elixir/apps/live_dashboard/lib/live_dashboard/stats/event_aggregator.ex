defmodule LiveDashboard.Stats.EventAggregator do
  @moduledoc """
  Tracks per-tenant rolling statistics from Kafka events.
  Resets daily counters at midnight UTC.
  """
  use GenServer
  require Logger

  def start_link(opts \\ []) do
    GenServer.start_link(__MODULE__, %{}, Keyword.merge([name: __MODULE__], opts))
  end

  @doc "Get current stats for a tenant."
  def get_stats(tenant_id, server \\ __MODULE__) do
    GenServer.call(server, {:get_stats, tenant_id})
  end

  @doc "Record an event for a tenant."
  def record_event(event_type, tenant_id, server \\ __MODULE__) do
    GenServer.cast(server, {:record_event, event_type, tenant_id})
  end

  # Server callbacks

  @impl true
  def init(_) do
    schedule_daily_reset()
    {:ok, %{}}
  end

  @impl true
  def handle_call({:get_stats, tenant_id}, _from, state) do
    stats = Map.get(state, tenant_id, default_stats())
    {:reply, stats, state}
  end

  @impl true
  def handle_cast({:record_event, event_type, tenant_id}, state) do
    stats = Map.get(state, tenant_id, default_stats())

    updated_stats = case event_type do
      "CLAIM_SUBMITTED" ->
        %{stats | claims_today: stats.claims_today + 1, claims_pending: stats.claims_pending + 1}
      "CLAIM_ADJUDICATED" ->
        %{stats | claims_pending: max(stats.claims_pending - 1, 0)}
      "PAYMENT_CREATED" ->
        %{stats | payments_today: stats.payments_today + 1}
      "MEMBER_ENROLLED" ->
        %{stats | members_active: stats.members_active + 1}
      _ ->
        stats
    end

    {:noreply, Map.put(state, tenant_id, updated_stats)}
  end

  @impl true
  def handle_info(:daily_reset, state) do
    Logger.info("Resetting daily counters")
    reset_state = state
    |> Enum.map(fn {tenant_id, stats} ->
      {tenant_id, %{stats | claims_today: 0, payments_today: 0}}
    end)
    |> Map.new()

    schedule_daily_reset()
    {:noreply, reset_state}
  end

  defp default_stats do
    %{
      claims_today: 0,
      claims_pending: 0,
      payments_today: 0,
      members_active: 0,
      last_updated: DateTime.utc_now() |> DateTime.to_iso8601()
    }
  end

  defp schedule_daily_reset do
    # Reset at midnight UTC
    now = DateTime.utc_now()
    midnight = DateTime.new!(Date.add(DateTime.to_date(now), 1), ~T[00:00:00], "Etc/UTC")
    ms_until = DateTime.diff(midnight, now, :millisecond)
    Process.send_after(self(), :daily_reset, ms_until)
  end
end
