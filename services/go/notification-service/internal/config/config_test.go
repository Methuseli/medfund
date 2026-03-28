package config

import "testing"

func TestLoad_Defaults(t *testing.T) {
	cfg := Load()
	if cfg.Port != "3001" {
		t.Fatalf("expected port 3001, got %s", cfg.Port)
	}
	if cfg.ConsumerGroupID != "notification-service" {
		t.Fatalf("expected group notification-service, got %s", cfg.ConsumerGroupID)
	}
	if cfg.SMSProvider != "mock" {
		t.Fatalf("expected SMS provider mock, got %s", cfg.SMSProvider)
	}
}
