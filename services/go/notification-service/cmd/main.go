package main

import (
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"

	"github.com/medfund/notification-service/internal/handler"
	"github.com/medfund/notification-service/internal/notification"
)

func main() {
	app := fiber.New(fiber.Config{
		AppName: "MedFund Notification Service",
	})

	app.Use(recover.New())
	app.Use(logger.New())

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{"status": "ok", "service": "notification-service"})
	})

	svc := notification.NewService()
	h := handler.New(svc)
	h.RegisterRoutes(app)

	port := os.Getenv("PORT")
	if port == "" {
		port = "3001"
	}

	log.Printf("Notification Service starting on port %s", port)
	log.Fatal(app.Listen(":" + port))
}
