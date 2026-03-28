package export

import (
	"strings"
	"testing"
)

func TestService_GeneratePDF(t *testing.T) {
	svc := NewService()
	data, err := svc.GeneratePDF("tenant-1", "invoice", nil)
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if len(data) == 0 {
		t.Fatal("expected non-empty PDF data")
	}
	if !strings.HasPrefix(string(data), "%PDF") {
		t.Fatal("expected PDF header")
	}
}

func TestService_GenerateCSV(t *testing.T) {
	svc := NewService()
	data, err := svc.GenerateCSV("tenant-1", []string{"name", "age"}, [][]string{
		{"John", "30"},
		{"Jane", "25"},
	})
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}

	csv := string(data)
	if !strings.Contains(csv, "name,age") {
		t.Fatal("expected CSV header")
	}
	if !strings.Contains(csv, "John,30") {
		t.Fatal("expected first row")
	}
	if !strings.Contains(csv, "Jane,25") {
		t.Fatal("expected second row")
	}
}

func TestService_GenerateCSV_Empty(t *testing.T) {
	svc := NewService()
	data, err := svc.GenerateCSV("tenant-1", []string{"id"}, nil)
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if !strings.Contains(string(data), "id") {
		t.Fatal("expected header even with no rows")
	}
}
