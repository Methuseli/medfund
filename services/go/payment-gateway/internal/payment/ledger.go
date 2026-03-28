package payment

import (
	"sync"
	"time"

	"github.com/google/uuid"
)

type Ledger struct {
	mu           sync.RWMutex
	transactions map[string]*Transaction
	idempotency  map[string]string // idempotencyKey -> transactionID
}

func NewLedger() *Ledger {
	return &Ledger{
		transactions: make(map[string]*Transaction),
		idempotency:  make(map[string]string),
	}
}

func (l *Ledger) CheckIdempotency(key string) (*Transaction, bool) {
	l.mu.RLock()
	defer l.mu.RUnlock()
	if txnID, ok := l.idempotency[key]; ok {
		if txn, exists := l.transactions[txnID]; exists {
			return txn, true
		}
	}
	return nil, false
}

func (l *Ledger) Record(req InitiateRequest, resp *InitiateResponse, provider string, direction string) *Transaction {
	l.mu.Lock()
	defer l.mu.Unlock()

	txn := &Transaction{
		ID:             uuid.New().String(),
		TenantID:       req.TenantID,
		Amount:         req.Amount,
		Currency:       req.Currency,
		Method:         req.Method,
		Status:         resp.Status,
		Reference:      req.Reference,
		ProviderRef:    resp.ProviderRef,
		Provider:       provider,
		IdempotencyKey: req.IdempotencyKey,
		Direction:      direction,
		CreatedAt:      time.Now(),
		UpdatedAt:      time.Now(),
	}

	l.transactions[txn.ID] = txn
	if req.IdempotencyKey != "" {
		l.idempotency[req.IdempotencyKey] = txn.ID
	}
	return txn
}

func (l *Ledger) GetByID(id string) (*Transaction, bool) {
	l.mu.RLock()
	defer l.mu.RUnlock()
	txn, ok := l.transactions[id]
	return txn, ok
}

func (l *Ledger) UpdateStatus(id string, status Status) {
	l.mu.Lock()
	defer l.mu.Unlock()
	if txn, ok := l.transactions[id]; ok {
		txn.Status = status
		txn.UpdatedAt = time.Now()
	}
}

func (l *Ledger) ListByTenant(tenantID string) []*Transaction {
	l.mu.RLock()
	defer l.mu.RUnlock()
	var result []*Transaction
	for _, txn := range l.transactions {
		if txn.TenantID == tenantID {
			result = append(result, txn)
		}
	}
	return result
}
