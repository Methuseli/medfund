package config

import (
	"os"
	"testing"
)

func TestLoad_Defaults(t *testing.T) {
	cfg := Load()
	if cfg.Port != "3000" {
		t.Fatalf("expected port 3000, got %s", cfg.Port)
	}
	if cfg.RateLimitPerMin != 120 {
		t.Fatalf("expected rate limit 120, got %d", cfg.RateLimitPerMin)
	}
	if cfg.TenancyServiceURL != "http://localhost:8081" {
		t.Fatalf("expected tenancy URL http://localhost:8081, got %s", cfg.TenancyServiceURL)
	}
}

func TestLoad_FromEnv(t *testing.T) {
	os.Setenv("PORT", "9000")
	defer os.Unsetenv("PORT")

	cfg := Load()
	if cfg.Port != "9000" {
		t.Fatalf("expected port 9000, got %s", cfg.Port)
	}
}
