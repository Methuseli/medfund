package handler

import (
	"net/http/httptest"
	"strings"
	"testing"

	"github.com/gofiber/fiber/v2"
	"github.com/medfund/notification-service/internal/notification"
)

func TestSendNotification_ValidRequest_Returns202(t *testing.T) {
	app := fiber.New()
	svc := notification.NewService()
	h := New(svc)
	h.RegisterRoutes(app)

	body := strings.NewReader(`{"channel":"email","recipient":"user@example.com","subject":"Test","body":"Hello"}`)
	req := httptest.NewRequest("POST", "/api/v1/notifications/send", body)
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-Tenant-ID", "test-tenant")

	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 202 {
		t.Fatalf("expected 202, got %d", resp.StatusCode)
	}
}

func TestSendNotification_SMSChannel_Returns202(t *testing.T) {
	app := fiber.New()
	svc := notification.NewService()
	h := New(svc)
	h.RegisterRoutes(app)

	body := strings.NewReader(`{"channel":"sms","recipient":"+263771234567","body":"Your OTP is 1234"}`)
	req := httptest.NewRequest("POST", "/api/v1/notifications/send", body)
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-Tenant-ID", "test-tenant")

	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 202 {
		t.Fatalf("expected 202, got %d", resp.StatusCode)
	}
}

func TestSendNotification_PushChannel_Returns202(t *testing.T) {
	app := fiber.New()
	svc := notification.NewService()
	h := New(svc)
	h.RegisterRoutes(app)

	body := strings.NewReader(`{"channel":"push","recipient":"device-token-123","subject":"Alert","body":"New claim submitted"}`)
	req := httptest.NewRequest("POST", "/api/v1/notifications/send", body)
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-Tenant-ID", "test-tenant")

	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 202 {
		t.Fatalf("expected 202, got %d", resp.StatusCode)
	}
}

func TestSendNotification_InvalidBody_Returns400(t *testing.T) {
	app := fiber.New()
	svc := notification.NewService()
	h := New(svc)
	h.RegisterRoutes(app)

	body := strings.NewReader("not json")
	req := httptest.NewRequest("POST", "/api/v1/notifications/send", body)
	req.Header.Set("Content-Type", "application/json")

	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 400 {
		t.Fatalf("expected 400, got %d", resp.StatusCode)
	}
}

func TestSendNotification_EmptyBody_Returns400(t *testing.T) {
	app := fiber.New()
	svc := notification.NewService()
	h := New(svc)
	h.RegisterRoutes(app)

	req := httptest.NewRequest("POST", "/api/v1/notifications/send", nil)
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-Tenant-ID", "test-tenant")

	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	// Empty body should fail to parse into Notification struct
	if resp.StatusCode != 400 {
		t.Fatalf("expected 400, got %d", resp.StatusCode)
	}
}

func TestSendNotification_TenantIDForwarded(t *testing.T) {
	app := fiber.New()
	svc := notification.NewService()
	h := New(svc)
	h.RegisterRoutes(app)

	body := strings.NewReader(`{"channel":"email","recipient":"user@example.com","subject":"Test","body":"Hello"}`)
	req := httptest.NewRequest("POST", "/api/v1/notifications/send", body)
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-Tenant-ID", "tenant-abc")

	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 202 {
		t.Fatalf("expected 202, got %d", resp.StatusCode)
	}
}
