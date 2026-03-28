package handler

import (
	"io"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/medfund/audit-service/internal/audit"
)

func setupApp() *fiber.App {
	app := fiber.New()
	return app
}

func TestQueryEvents_EmptyStore_ReturnsOK(t *testing.T) {
	app := setupApp()
	store := audit.NewStore()
	h := New(store)
	h.RegisterRoutes(app)

	req := httptest.NewRequest("GET", "/api/v1/audit/events", nil)
	req.Header.Set("X-Tenant-ID", "test-tenant")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestQueryEvents_WithEntityTypeFilter(t *testing.T) {
	app := setupApp()
	store := audit.NewStore()
	store.Append(audit.Event{ID: "1", TenantID: "t1", EntityType: "Member", Action: "CREATE", Timestamp: time.Now()})
	store.Append(audit.Event{ID: "2", TenantID: "t1", EntityType: "Claim", Action: "CREATE", Timestamp: time.Now()})
	h := New(store)
	h.RegisterRoutes(app)

	req := httptest.NewRequest("GET", "/api/v1/audit/events?entityType=Member", nil)
	req.Header.Set("X-Tenant-ID", "t1")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}

	body, _ := io.ReadAll(resp.Body)
	if !strings.Contains(string(body), "Member") {
		t.Fatal("expected response to contain Member")
	}
	if strings.Contains(string(body), "Claim") {
		t.Fatal("expected response NOT to contain Claim when filtering by Member")
	}
}

func TestQueryEvents_WithActionFilter(t *testing.T) {
	app := setupApp()
	store := audit.NewStore()
	store.Append(audit.Event{ID: "1", TenantID: "t1", EntityType: "Member", Action: "CREATE", Timestamp: time.Now()})
	store.Append(audit.Event{ID: "2", TenantID: "t1", EntityType: "Member", Action: "UPDATE", Timestamp: time.Now()})
	h := New(store)
	h.RegisterRoutes(app)

	req := httptest.NewRequest("GET", "/api/v1/audit/events?action=UPDATE", nil)
	req.Header.Set("X-Tenant-ID", "t1")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}

	body, _ := io.ReadAll(resp.Body)
	if !strings.Contains(string(body), "UPDATE") {
		t.Fatal("expected response to contain UPDATE")
	}
}

func TestQueryEvents_TenantIsolation(t *testing.T) {
	app := setupApp()
	store := audit.NewStore()
	store.Append(audit.Event{ID: "1", TenantID: "tenant-a", EntityType: "Member", Action: "CREATE", Timestamp: time.Now()})
	store.Append(audit.Event{ID: "2", TenantID: "tenant-b", EntityType: "Claim", Action: "CREATE", Timestamp: time.Now()})
	h := New(store)
	h.RegisterRoutes(app)

	req := httptest.NewRequest("GET", "/api/v1/audit/events", nil)
	req.Header.Set("X-Tenant-ID", "tenant-a")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}

	body, _ := io.ReadAll(resp.Body)
	if !strings.Contains(string(body), "tenant-a") {
		t.Fatal("expected response to contain tenant-a events")
	}
	if strings.Contains(string(body), "tenant-b") {
		t.Fatal("expected response NOT to contain tenant-b events")
	}
}

func TestQueryEvents_WithPagination(t *testing.T) {
	app := setupApp()
	store := audit.NewStore()
	for i := 0; i < 5; i++ {
		store.Append(audit.Event{
			ID:         string(rune('A' + i)),
			TenantID:   "t1",
			EntityType: "Member",
			Action:     "CREATE",
			Timestamp:  time.Now().Add(time.Duration(i) * time.Second),
		})
	}
	h := New(store)
	h.RegisterRoutes(app)

	req := httptest.NewRequest("GET", "/api/v1/audit/events?page=1&pageSize=2", nil)
	req.Header.Set("X-Tenant-ID", "t1")
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}
}

func TestGetStats_EmptyStore_ReturnsZero(t *testing.T) {
	app := setupApp()
	store := audit.NewStore()
	h := New(store)
	h.RegisterRoutes(app)

	req := httptest.NewRequest("GET", "/api/v1/audit/stats", nil)
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}

	body, _ := io.ReadAll(resp.Body)
	if !strings.Contains(string(body), "0") {
		t.Fatalf("expected totalEvents=0, got %s", string(body))
	}
}

func TestGetStats_WithData_ReturnsCount(t *testing.T) {
	app := setupApp()
	store := audit.NewStore()
	store.Append(audit.Event{ID: "1", TenantID: "t1", Timestamp: time.Now()})
	store.Append(audit.Event{ID: "2", TenantID: "t1", Timestamp: time.Now()})
	store.Append(audit.Event{ID: "3", TenantID: "t1", Timestamp: time.Now()})
	h := New(store)
	h.RegisterRoutes(app)

	req := httptest.NewRequest("GET", "/api/v1/audit/stats", nil)
	resp, err := app.Test(req)
	if err != nil {
		t.Fatal(err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", resp.StatusCode)
	}

	body, _ := io.ReadAll(resp.Body)
	if !strings.Contains(string(body), "3") {
		t.Fatalf("expected totalEvents=3, got %s", string(body))
	}
}
