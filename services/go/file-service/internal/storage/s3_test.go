package storage

import (
	"strings"
	"testing"
	"time"
)

func TestMockStorage_GenerateUploadURL(t *testing.T) {
	s := NewMockStorage()
	url, err := s.GenerateUploadURL("tenant-1", "test.pdf", "application/pdf")
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if url.URL == "" {
		t.Fatal("expected non-empty URL")
	}
	if !strings.Contains(url.Key, "tenant-1") {
		t.Fatalf("expected key to contain tenant ID, got %s", url.Key)
	}
	if !strings.Contains(url.Key, "test.pdf") {
		t.Fatalf("expected key to contain filename, got %s", url.Key)
	}
	if url.ExpiresAt.Before(time.Now()) {
		t.Fatal("expected expiry in the future")
	}
}

func TestMockStorage_GenerateDownloadURL(t *testing.T) {
	s := NewMockStorage()
	url, err := s.GenerateDownloadURL("tenant-1", "tenant-1/file.pdf")
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if url.URL == "" {
		t.Fatal("expected non-empty URL")
	}
	if url.ExpiresAt.Before(time.Now()) {
		t.Fatal("expected expiry in the future")
	}
}

func TestMockStorage_Delete(t *testing.T) {
	s := NewMockStorage()
	err := s.Delete("tenant-1", "some-key")
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
}
