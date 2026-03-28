package handler

import (
	"github.com/gofiber/fiber/v2"
	"github.com/medfund/notification-service/internal/notification"
)

type Handler struct {
	service *notification.Service
}

func New(service *notification.Service) *Handler {
	return &Handler{service: service}
}

func (h *Handler) SendNotification(c *fiber.Ctx) error {
	var n notification.Notification
	if err := c.BodyParser(&n); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}
	tenantID := c.Get("X-Tenant-ID")
	n.TenantID = tenantID

	if err := h.service.Send(n); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.Status(fiber.StatusAccepted).JSON(fiber.Map{"status": "queued"})
}

func (h *Handler) RegisterRoutes(app *fiber.App) {
	api := app.Group("/api/v1/notifications")
	api.Post("/send", h.SendNotification)
}
