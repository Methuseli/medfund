package config

import "testing"

func TestLoad_Defaults(t *testing.T) {
	cfg := Load()
	if cfg.Port != "3003" {
		t.Fatalf("expected port 3003, got %s", cfg.Port)
	}
	if cfg.S3Region != "us-east-1" {
		t.Fatalf("expected region us-east-1, got %s", cfg.S3Region)
	}
	if cfg.MaxUploadSizeMB != "50" {
		t.Fatalf("expected max upload 50, got %s", cfg.MaxUploadSizeMB)
	}
}
