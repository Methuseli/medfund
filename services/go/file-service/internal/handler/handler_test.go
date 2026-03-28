package handler

import (
	"net/http/httptest"
	"testing"

	"github.com/gofiber/fiber/v2"
	"github.com/medfund/file-service/internal/export"
	"github.com/medfund/file-service/internal/storage"
)

func setupApp() *fiber.App {
	app := fiber.New()
	store := storage.NewMockStorage()
	exportSvc := export.NewService()
	h := New(store, exportSvc)
	h.RegisterRoutes(app)
	return app
}

func TestGetUploadURL_ValidFilename_Returns200(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/upload-url?filename=test.pdf", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestGetUploadURL_MissingFilename_Returns400(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/upload-url", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 400 {
		t.Fatalf("expected 400, got %d", resp.StatusCode)
	}
}

func TestGetUploadURL_WithContentType_Returns200(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/upload-url?filename=photo.jpg&contentType=image/jpeg", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestGetDownloadURL_Returns200(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/download-url?key=tenant-1/file.pdf", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestGetDownloadURL_MissingKey_Returns400(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/download-url", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 400 {
		t.Fatalf("expected 400, got %d", resp.StatusCode)
	}
}

func TestDeleteFile_Returns200(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("DELETE", "/api/v1/files?key=some-key", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestExportPDF_Returns200(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/export/pdf", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
	ct := resp.Header.Get("Content-Type")
	if ct != "application/pdf" {
		t.Fatalf("expected Content-Type application/pdf, got %s", ct)
	}
}

func TestExportPDF_WithTemplate_Returns200(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/export/pdf?template=claim_report", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestExportCSV_Returns200(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/export/csv", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
	ct := resp.Header.Get("Content-Type")
	if ct != "text/csv" {
		t.Fatalf("expected Content-Type text/csv, got %s", ct)
	}
}

func TestExportCSV_HasContentDisposition(t *testing.T) {
	app := setupApp()
	req := httptest.NewRequest("GET", "/api/v1/files/export/csv", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	cd := resp.Header.Get("Content-Disposition")
	if cd == "" {
		t.Fatal("expected Content-Disposition header to be set")
	}
}
