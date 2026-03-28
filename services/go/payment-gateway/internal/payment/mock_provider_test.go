package payment

import (
	"testing"
)

func TestMockProvider_Name(t *testing.T) {
	p := NewMockProvider()
	if p.Name() != "mock" {
		t.Fatalf("expected mock, got %s", p.Name())
	}
}

func TestMockProvider_Initiate(t *testing.T) {
	p := NewMockProvider()
	resp, err := p.Initiate(InitiateRequest{Amount: 100, Currency: "USD"})
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if resp.Status != StatusCompleted {
		t.Fatalf("expected completed, got %s", resp.Status)
	}
	if resp.ProviderRef == "" {
		t.Fatal("expected non-empty provider ref")
	}
}

func TestMockProvider_CheckStatus(t *testing.T) {
	p := NewMockProvider()
	status, err := p.CheckStatus("any-ref")
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if status != StatusCompleted {
		t.Fatalf("expected completed, got %s", status)
	}
}

func TestMockProvider_VerifyWebhook(t *testing.T) {
	p := NewMockProvider()
	if !p.VerifyWebhook([]byte("payload"), "sig") {
		t.Fatal("mock webhook verification should always return true")
	}
}
