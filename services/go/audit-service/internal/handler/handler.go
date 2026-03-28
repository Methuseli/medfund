package handler

import (
	"strconv"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/medfund/audit-service/internal/audit"
)

type Handler struct {
	store *audit.Store
}

func New(store *audit.Store) *Handler {
	return &Handler{store: store}
}

func (h *Handler) QueryEvents(c *fiber.Ctx) error {
	filter := audit.QueryFilter{
		TenantID:   c.Get("X-Tenant-ID"),
		EntityType: c.Query("entityType"),
		EntityID:   c.Query("entityId"),
		Action:     c.Query("action"),
		ActorID:    c.Query("actorId"),
	}

	if start := c.Query("startDate"); start != "" {
		if t, err := time.Parse("2006-01-02", start); err == nil {
			filter.StartDate = t
		}
	}
	if end := c.Query("endDate"); end != "" {
		if t, err := time.Parse("2006-01-02", end); err == nil {
			filter.EndDate = t.Add(24 * time.Hour) // inclusive
		}
	}
	if p := c.Query("page"); p != "" {
		filter.Page, _ = strconv.Atoi(p)
	}
	if ps := c.Query("pageSize"); ps != "" {
		filter.PageSize, _ = strconv.Atoi(ps)
	}

	events := h.store.Query(filter)
	return c.JSON(fiber.Map{
		"events": events,
		"total":  h.store.Count(),
		"page":   filter.Page,
	})
}

func (h *Handler) GetStats(c *fiber.Ctx) error {
	return c.JSON(fiber.Map{
		"totalEvents": h.store.Count(),
	})
}

func (h *Handler) RegisterRoutes(app *fiber.App) {
	api := app.Group("/api/v1/audit")
	api.Get("/events", h.QueryEvents)
	api.Get("/stats", h.GetStats)
}
