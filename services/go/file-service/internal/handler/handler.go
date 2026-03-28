package handler

import (
	"github.com/gofiber/fiber/v2"
	"github.com/medfund/file-service/internal/export"
	"github.com/medfund/file-service/internal/storage"
)

type Handler struct {
	storage storage.Storage
	export  *export.Service
}

func New(storage storage.Storage, exportSvc *export.Service) *Handler {
	return &Handler{storage: storage, export: exportSvc}
}

func (h *Handler) GetUploadURL(c *fiber.Ctx) error {
	tenantID := c.Get("X-Tenant-ID")
	filename := c.Query("filename")
	contentType := c.Query("contentType", "application/octet-stream")

	if filename == "" {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "filename is required"})
	}

	url, err := h.storage.GenerateUploadURL(tenantID, filename, contentType)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.JSON(url)
}

func (h *Handler) GetDownloadURL(c *fiber.Ctx) error {
	tenantID := c.Get("X-Tenant-ID")
	key := c.Query("key")

	if key == "" {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "key is required"})
	}

	url, err := h.storage.GenerateDownloadURL(tenantID, key)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.JSON(url)
}

func (h *Handler) DeleteFile(c *fiber.Ctx) error {
	tenantID := c.Get("X-Tenant-ID")
	key := c.Query("key")

	if err := h.storage.Delete(tenantID, key); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.JSON(fiber.Map{"status": "deleted"})
}

func (h *Handler) ExportPDF(c *fiber.Ctx) error {
	tenantID := c.Get("X-Tenant-ID")
	templateType := c.Query("template", "invoice")

	data, err := h.export.GeneratePDF(tenantID, templateType, nil)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	c.Set("Content-Type", "application/pdf")
	c.Set("Content-Disposition", "attachment; filename=export.pdf")
	return c.Send(data)
}

func (h *Handler) ExportCSV(c *fiber.Ctx) error {
	tenantID := c.Get("X-Tenant-ID")

	data, err := h.export.GenerateCSV(tenantID, []string{"id", "name"}, [][]string{{"1", "sample"}})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	c.Set("Content-Type", "text/csv")
	c.Set("Content-Disposition", "attachment; filename=export.csv")
	return c.Send(data)
}

func (h *Handler) RegisterRoutes(app *fiber.App) {
	api := app.Group("/api/v1/files")
	api.Get("/upload-url", h.GetUploadURL)
	api.Get("/download-url", h.GetDownloadURL)
	api.Delete("/", h.DeleteFile)
	api.Get("/export/pdf", h.ExportPDF)
	api.Get("/export/csv", h.ExportCSV)
}
