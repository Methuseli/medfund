package config

import "testing"

func TestLoad_Defaults(t *testing.T) {
	cfg := Load()
	if cfg.Port != "3002" {
		t.Fatalf("expected port 3002, got %s", cfg.Port)
	}
	if cfg.ConsumerGroupID != "audit-service" {
		t.Fatalf("expected group audit-service, got %s", cfg.ConsumerGroupID)
	}
}
