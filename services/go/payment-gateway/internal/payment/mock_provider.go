package payment

import (
	"fmt"
	"time"
)

type MockProvider struct{}

func NewMockProvider() *MockProvider { return &MockProvider{} }

func (m *MockProvider) Name() string { return "mock" }

func (m *MockProvider) Initiate(req InitiateRequest) (*InitiateResponse, error) {
	return &InitiateResponse{
		TransactionID: fmt.Sprintf("mock-%d", time.Now().UnixNano()),
		Status:        StatusCompleted,
		ProviderRef:   fmt.Sprintf("MOCK-REF-%d", time.Now().UnixNano()),
	}, nil
}

func (m *MockProvider) CheckStatus(providerRef string) (Status, error) {
	return StatusCompleted, nil
}

func (m *MockProvider) VerifyWebhook(payload []byte, signature string) bool {
	return true
}
