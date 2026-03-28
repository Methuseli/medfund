package middleware

import (
	"net/http/httptest"
	"testing"

	"github.com/gofiber/fiber/v2"
)

func TestRateLimiter_AllowsUnderLimit(t *testing.T) {
	app := fiber.New()
	app.Use(NewRateLimiter(10)) // 10 per minute
	app.Get("/test", func(c *fiber.Ctx) error {
		return c.SendString("ok")
	})

	for i := 0; i < 10; i++ {
		req := httptest.NewRequest("GET", "/test", nil)
		resp, err := app.Test(req)
		if err != nil {
			t.Fatalf("request %d failed: %v", i, err)
		}
		if resp.StatusCode != 200 {
			t.Fatalf("request %d: expected 200, got %d", i, resp.StatusCode)
		}
	}
}

func TestRateLimiter_BlocksOverLimit(t *testing.T) {
	app := fiber.New()
	app.Use(NewRateLimiter(5))
	app.Get("/test", func(c *fiber.Ctx) error {
		return c.SendString("ok")
	})

	// Send 6 requests — 6th should be blocked
	var lastStatus int
	for i := 0; i < 6; i++ {
		req := httptest.NewRequest("GET", "/test", nil)
		resp, err := app.Test(req)
		if err != nil {
			t.Fatalf("request %d failed: %v", i, err)
		}
		lastStatus = resp.StatusCode
	}
	if lastStatus != 429 {
		t.Fatalf("expected 429 on 6th request, got %d", lastStatus)
	}
}
