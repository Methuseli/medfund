defmodule LiveDashboard.Stats.EventAggregatorTest do
  use ExUnit.Case, async: true

  alias LiveDashboard.Stats.EventAggregator

  setup do
    {:ok, pid} = EventAggregator.start_link(name: :"test_aggregator_#{:erlang.unique_integer()}")
    %{pid: pid}
  end

  test "returns default stats for unknown tenant", %{pid: pid} do
    stats = EventAggregator.get_stats("unknown-tenant", pid)
    assert stats.claims_today == 0
    assert stats.claims_pending == 0
    assert stats.payments_today == 0
    assert stats.members_active == 0
  end

  test "increments claims_today on CLAIM_SUBMITTED", %{pid: pid} do
    EventAggregator.record_event("CLAIM_SUBMITTED", "t1", pid)
    EventAggregator.record_event("CLAIM_SUBMITTED", "t1", pid)
    # Give GenServer time to process
    :timer.sleep(10)
    stats = EventAggregator.get_stats("t1", pid)
    assert stats.claims_today == 2
    assert stats.claims_pending == 2
  end

  test "decrements claims_pending on CLAIM_ADJUDICATED", %{pid: pid} do
    EventAggregator.record_event("CLAIM_SUBMITTED", "t1", pid)
    EventAggregator.record_event("CLAIM_ADJUDICATED", "t1", pid)
    :timer.sleep(10)
    stats = EventAggregator.get_stats("t1", pid)
    assert stats.claims_pending == 0
  end

  test "increments payments_today on PAYMENT_CREATED", %{pid: pid} do
    EventAggregator.record_event("PAYMENT_CREATED", "t1", pid)
    :timer.sleep(10)
    stats = EventAggregator.get_stats("t1", pid)
    assert stats.payments_today == 1
  end

  test "increments members_active on MEMBER_ENROLLED", %{pid: pid} do
    EventAggregator.record_event("MEMBER_ENROLLED", "t1", pid)
    :timer.sleep(10)
    stats = EventAggregator.get_stats("t1", pid)
    assert stats.members_active == 1
  end

  test "isolates tenant stats", %{pid: pid} do
    EventAggregator.record_event("CLAIM_SUBMITTED", "t1", pid)
    EventAggregator.record_event("CLAIM_SUBMITTED", "t2", pid)
    EventAggregator.record_event("CLAIM_SUBMITTED", "t2", pid)
    :timer.sleep(10)

    assert EventAggregator.get_stats("t1", pid).claims_today == 1
    assert EventAggregator.get_stats("t2", pid).claims_today == 2
  end
end
