package payment

import (
	"testing"
)

func TestLedger_Record_And_GetByID(t *testing.T) {
	ledger := NewLedger()
	req := InitiateRequest{
		TenantID:       "t1",
		Amount:         100.00,
		Currency:       "USD",
		Method:         "card",
		Reference:      "REF-001",
		IdempotencyKey: "idem-001",
	}
	resp := &InitiateResponse{
		TransactionID: "txn-1",
		Status:        StatusCompleted,
		ProviderRef:   "PROV-001",
	}

	txn := ledger.Record(req, resp, "mock", "inbound")
	if txn.ID == "" {
		t.Fatal("expected non-empty transaction ID")
	}
	if txn.Amount != 100.00 {
		t.Fatalf("expected amount 100, got %f", txn.Amount)
	}
	if txn.Status != StatusCompleted {
		t.Fatalf("expected status completed, got %s", txn.Status)
	}

	// GetByID
	found, ok := ledger.GetByID(txn.ID)
	if !ok {
		t.Fatal("expected to find transaction")
	}
	if found.Reference != "REF-001" {
		t.Fatalf("expected reference REF-001, got %s", found.Reference)
	}
}

func TestLedger_Idempotency(t *testing.T) {
	ledger := NewLedger()
	req := InitiateRequest{
		TenantID:       "t1",
		Amount:         50.00,
		Currency:       "USD",
		IdempotencyKey: "idem-duplicate",
	}
	resp := &InitiateResponse{Status: StatusCompleted, ProviderRef: "P1"}

	txn1 := ledger.Record(req, resp, "mock", "inbound")

	// Check idempotency returns existing
	existing, found := ledger.CheckIdempotency("idem-duplicate")
	if !found {
		t.Fatal("expected idempotency hit")
	}
	if existing.ID != txn1.ID {
		t.Fatalf("expected same transaction ID, got %s vs %s", existing.ID, txn1.ID)
	}
}

func TestLedger_IdempotencyMiss(t *testing.T) {
	ledger := NewLedger()
	_, found := ledger.CheckIdempotency("nonexistent")
	if found {
		t.Fatal("expected idempotency miss")
	}
}

func TestLedger_UpdateStatus(t *testing.T) {
	ledger := NewLedger()
	req := InitiateRequest{TenantID: "t1", Amount: 75.00}
	resp := &InitiateResponse{Status: StatusPending, ProviderRef: "P2"}
	txn := ledger.Record(req, resp, "mock", "inbound")

	ledger.UpdateStatus(txn.ID, StatusCompleted)

	updated, _ := ledger.GetByID(txn.ID)
	if updated.Status != StatusCompleted {
		t.Fatalf("expected completed, got %s", updated.Status)
	}
}

func TestLedger_ListByTenant(t *testing.T) {
	ledger := NewLedger()
	resp := &InitiateResponse{Status: StatusCompleted, ProviderRef: "P"}

	ledger.Record(InitiateRequest{TenantID: "t1", Amount: 10}, resp, "mock", "inbound")
	ledger.Record(InitiateRequest{TenantID: "t2", Amount: 20}, resp, "mock", "inbound")
	ledger.Record(InitiateRequest{TenantID: "t1", Amount: 30}, resp, "mock", "inbound")

	t1Txns := ledger.ListByTenant("t1")
	if len(t1Txns) != 2 {
		t.Fatalf("expected 2 transactions for t1, got %d", len(t1Txns))
	}

	t2Txns := ledger.ListByTenant("t2")
	if len(t2Txns) != 1 {
		t.Fatalf("expected 1 transaction for t2, got %d", len(t2Txns))
	}
}

func TestLedger_GetByID_NotFound(t *testing.T) {
	ledger := NewLedger()
	_, found := ledger.GetByID("nonexistent")
	if found {
		t.Fatal("expected not found")
	}
}
