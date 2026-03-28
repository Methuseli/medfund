package middleware

import (
	"net/http/httptest"
	"testing"

	"github.com/gofiber/fiber/v2"
)

func TestTenantResolver_WithHeader(t *testing.T) {
	app := fiber.New()
	app.Use(TenantResolver())
	app.Get("/api/v1/members", func(c *fiber.Ctx) error {
		return c.SendString(c.Get("X-Tenant-ID"))
	})

	req := httptest.NewRequest("GET", "/api/v1/members", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestTenantResolver_MissingTenant_Returns400(t *testing.T) {
	app := fiber.New()
	app.Use(TenantResolver())
	app.Get("/api/v1/members", func(c *fiber.Ctx) error {
		return c.SendString("ok")
	})

	req := httptest.NewRequest("GET", "/api/v1/members", nil)
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 400 {
		t.Fatalf("expected 400, got %d", resp.StatusCode)
	}
}

func TestTenantResolver_HealthPath_SkipsTenantCheck(t *testing.T) {
	app := fiber.New()
	app.Use(TenantResolver())
	app.Get("/health", func(c *fiber.Ctx) error {
		return c.SendString("ok")
	})

	req := httptest.NewRequest("GET", "/health", nil)
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestTenantResolver_TenancyEndpoint_SkipsTenantCheck(t *testing.T) {
	app := fiber.New()
	app.Use(TenantResolver())
	app.Get("/api/v1/tenants", func(c *fiber.Ctx) error {
		return c.SendString("ok")
	})

	req := httptest.NewRequest("GET", "/api/v1/tenants", nil)
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}
