package main

import (
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"

	"github.com/medfund/audit-service/internal/audit"
	"github.com/medfund/audit-service/internal/handler"
)

func main() {
	app := fiber.New(fiber.Config{
		AppName: "MedFund Audit Service",
	})

	app.Use(recover.New())
	app.Use(logger.New())

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{"status": "ok", "service": "audit-service"})
	})

	store := audit.NewStore()
	h := handler.New(store)
	h.RegisterRoutes(app)

	port := os.Getenv("PORT")
	if port == "" {
		port = "3002"
	}

	log.Printf("Audit Service starting on port %s", port)
	log.Fatal(app.Listen(":" + port))
}
