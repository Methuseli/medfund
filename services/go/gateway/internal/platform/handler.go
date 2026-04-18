package platform

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"sync"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/medfund/gateway/internal/config"
)

// Handler handles all /api/v1/platform/* routes. It aggregates data from
// backend services, performing real health checks and forwarding auth where needed.
type Handler struct {
	cfg    *config.Config
	client *http.Client
}

func NewHandler(cfg *config.Config) *Handler {
	return &Handler{
		cfg:    cfg,
		client: &http.Client{Timeout: 5 * time.Second},
	}
}

// Register mounts all platform routes on the given Fiber router group.
func (h *Handler) Register(router fiber.Router) {
	router.Get("/stats", h.getStats)
	router.Get("/health", h.getHealth)
	router.Get("/activity", h.getActivity)
	router.Get("/analytics/tenant-growth", h.getTenantGrowth)
	router.Get("/analytics/claims-distribution", h.getClaimsDistribution)
	router.Get("/analytics/claims-over-time", h.getClaimsOverTime)
	router.Get("/analytics/revenue-by-tenant", h.getRevenueByTenant)
	router.Get("/analytics/member-growth", h.getMemberGrowth)
}

// ── Stats ─────────────────────────────────────────────────────────────────────

type statsResponse struct {
	TotalTenants   int     `json:"totalTenants"`
	ActiveUsers    int     `json:"activeUsers"`
	TotalClaims    int     `json:"totalClaims"`
	MonthlyRevenue float64 `json:"monthlyRevenue"`
	TenantGrowth   float64 `json:"tenantGrowth"`
	UserGrowth     float64 `json:"userGrowth"`
	ClaimsGrowth   float64 `json:"claimsGrowth"`
	RevenueGrowth  float64 `json:"revenueGrowth"`
}

func (h *Handler) getStats(c *fiber.Ctx) error {
	token := c.Cookies("access_token")

	// Run backend calls concurrently
	type result struct {
		tenants int
		members int
		claims  int
	}
	ch := make(chan result, 1)

	go func() {
		var wg sync.WaitGroup
		var tenants, members, claims int

		wg.Add(3)
		go func() {
			defer wg.Done()
			tenants = h.fetchArrayLen(h.cfg.TenancyServiceURL+"/api/v1/tenants", token)
		}()
		go func() {
			defer wg.Done()
			members = h.fetchArrayLen(h.cfg.UserServiceURL+"/api/v1/members", token)
		}()
		go func() {
			defer wg.Done()
			claims = h.fetchArrayLen(h.cfg.ClaimsServiceURL+"/api/v1/claims", token)
		}()

		wg.Wait()
		ch <- result{tenants, members, claims}
	}()

	r := <-ch
	return c.JSON(statsResponse{
		TotalTenants:   r.tenants,
		ActiveUsers:    r.members,
		TotalClaims:    r.claims,
		MonthlyRevenue: 0,
		TenantGrowth:   0,
		UserGrowth:     0,
		ClaimsGrowth:   0,
		RevenueGrowth:  0,
	})
}

// ── Health ────────────────────────────────────────────────────────────────────

type serviceHealth struct {
	Service     string `json:"service"`
	DisplayName string `json:"displayName"`
	Description string `json:"description"`
	Category    string `json:"category"`
	Status      string `json:"status"`
	Latency     int    `json:"latency"`
}

type serviceSpec struct {
	key         string
	url         string
	displayName string
	description string
	category    string
}

func (h *Handler) getHealth(c *fiber.Ctx) error {
	checks := []serviceSpec{
		{
			"tenancy-service", h.cfg.TenancyServiceURL + "/actuator/health",
			"Tenant Management", "Handles tenant onboarding, plans, schema provisioning and configuration", "core",
		},
		{
			"user-service", h.cfg.UserServiceURL + "/actuator/health",
			"User Service", "Manages members, providers, groups, staff users and Keycloak sync", "core",
		},
		{
			"claims-service", h.cfg.ClaimsServiceURL + "/actuator/health",
			"Claims Processing", "Handles claim submission, adjudication, pre-authorisations and tariffs", "core",
		},
		{
			"contributions-service", h.cfg.ContribServiceURL + "/actuator/health",
			"Contributions", "Manages schemes, member contributions, invoices and transactions", "core",
		},
		{
			"finance-service", h.cfg.FinanceServiceURL + "/actuator/health",
			"Finance", "Processes payments, payment runs, provider balances and reconciliations", "core",
		},
		{
			"notification-service", h.cfg.NotifServiceURL + "/health",
			"Notifications", "Delivers email, SMS and in-app notifications via Kafka events", "support",
		},
		{
			"audit-service", h.cfg.AuditServiceURL + "/health",
			"Audit Log", "Stores immutable audit trail for all entity mutations across the platform", "support",
		},
		{
			"file-service", h.cfg.FileServiceURL + "/health",
			"File Storage", "Generates pre-signed S3 upload/download URLs and handles document exports", "support",
		},
		{
			"ai-service", "http://localhost:8000/health",
			"AI & Analytics", "Provides AI-assisted adjudication, fraud detection, OCR and chatbot", "support",
		},
		{
			"live-dashboard", "http://localhost:4000/api/v1/dashboard/health",
			"Live Dashboard", "Real-time Phoenix/WebSocket service for live operational dashboards", "support",
		},
		{
			"chat-service", "http://localhost:4001/api/v1/chat/health",
			"Chat Service", "Internal messaging and support chat powered by Phoenix Channels", "support",
		},
	}

	type indexed struct {
		i int
		s serviceHealth
	}

	results := make([]serviceHealth, len(checks))
	ch := make(chan indexed, len(checks))
	var wg sync.WaitGroup

	for i, svc := range checks {
		wg.Add(1)
		go func(idx int, spec serviceSpec) {
			defer wg.Done()
			start := time.Now()
			resp, err := h.client.Get(spec.url)
			latency := int(time.Since(start).Milliseconds())
			status := "down"
			if err == nil {
				resp.Body.Close()
				switch {
				case resp.StatusCode == 200:
					status = "healthy"
				case resp.StatusCode < 500:
					status = "degraded"
				}
			}
			ch <- indexed{idx, serviceHealth{
				Service:     spec.key,
				DisplayName: spec.displayName,
				Description: spec.description,
				Category:    spec.category,
				Status:      status,
				Latency:     latency,
			}}
		}(i, svc)
	}

	wg.Wait()
	close(ch)
	for r := range ch {
		results[r.i] = r.s
	}

	return c.JSON(results)
}

// ── Activity ──────────────────────────────────────────────────────────────────

type activityItem struct {
	ID          string `json:"id"`
	Type        string `json:"type"`
	Description string `json:"description"`
	Actor       string `json:"actor"`
	Timestamp   string `json:"timestamp"`
	Severity    string `json:"severity"`
}

func (h *Handler) getActivity(c *fiber.Ctx) error {
	// Pull recent audit events from the audit service and map them to activity items.
	token := c.Cookies("access_token")
	url := h.cfg.AuditServiceURL + "/api/v1/audit/events?pageSize=10&page=0"

	body, err := h.fetchJSON(url, token)
	if err != nil {
		return c.JSON([]activityItem{})
	}

	var raw struct {
		Events []struct {
			ID         string `json:"id"`
			Action     string `json:"action"`
			EntityType string `json:"entityType"`
			EntityID   string `json:"entityId"`
			ActorID    string `json:"actorId"`
			OccurredAt string `json:"occurredAt"`
		} `json:"events"`
	}
	if err := json.Unmarshal(body, &raw); err != nil {
		return c.JSON([]activityItem{})
	}

	items := make([]activityItem, 0, len(raw.Events))
	for _, e := range raw.Events {
		items = append(items, activityItem{
			ID:          e.ID,
			Type:        e.Action,
			Description: fmt.Sprintf("%s %s %s", e.Action, e.EntityType, e.EntityID),
			Actor:       e.ActorID,
			Timestamp:   e.OccurredAt,
			Severity:    "info",
		})
	}
	return c.JSON(items)
}

// ── Analytics ─────────────────────────────────────────────────────────────────

type growthPoint struct {
	Month string `json:"month"`
	Count int    `json:"count"`
}

type distributionItem struct {
	Status string `json:"status"`
	Count  int    `json:"count"`
	Color  string `json:"color"`
}

func (h *Handler) getTenantGrowth(c *fiber.Ctx) error {
	months := []string{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}
	out := make([]growthPoint, len(months))
	for i, m := range months {
		out[i] = growthPoint{Month: m, Count: 0}
	}
	return c.JSON(out)
}

func (h *Handler) getClaimsDistribution(c *fiber.Ctx) error {
	return c.JSON([]distributionItem{
		{Status: "approved", Count: 0, Color: "#2EC4B6"},
		{Status: "pending", Count: 0, Color: "#FF9F1C"},
		{Status: "rejected", Count: 0, Color: "#E71D36"},
		{Status: "in_adjudication", Count: 0, Color: "#00B4D8"},
		{Status: "submitted", Count: 0, Color: "#90E0EF"},
	})
}

func (h *Handler) getClaimsOverTime(c *fiber.Ctx) error {
	return c.JSON([]growthPoint{})
}

func (h *Handler) getRevenueByTenant(c *fiber.Ctx) error {
	return c.JSON([]fiber.Map{})
}

func (h *Handler) getMemberGrowth(c *fiber.Ctx) error {
	return c.JSON([]growthPoint{})
}

// ── Helpers ───────────────────────────────────────────────────────────────────

// fetchJSON GETs a URL with an optional Bearer token and returns the raw body.
func (h *Handler) fetchJSON(url, token string) ([]byte, error) {
	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}
	if token != "" {
		req.Header.Set("Authorization", "Bearer "+token)
	}
	resp, err := h.client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("upstream %d", resp.StatusCode)
	}
	return io.ReadAll(resp.Body)
}

// fetchArrayLen calls a list endpoint and returns the length of the JSON array.
func (h *Handler) fetchArrayLen(url, token string) int {
	body, err := h.fetchJSON(url, token)
	if err != nil {
		return 0
	}
	var items []json.RawMessage
	if err := json.Unmarshal(body, &items); err != nil {
		return 0
	}
	return len(items)
}
