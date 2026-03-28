package config

import "testing"

func TestLoad_Defaults(t *testing.T) {
	cfg := Load()
	if cfg.Port != "3004" {
		t.Fatalf("expected port 3004, got %s", cfg.Port)
	}
}
