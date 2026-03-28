package main

import (
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"

	exportpkg "github.com/medfund/file-service/internal/export"
	"github.com/medfund/file-service/internal/handler"
	"github.com/medfund/file-service/internal/storage"
)

func main() {
	app := fiber.New(fiber.Config{
		AppName: "MedFund File Service",
	})

	app.Use(recover.New())
	app.Use(logger.New())

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{"status": "ok", "service": "file-service"})
	})

	store := storage.NewMockStorage()
	exportSvc := exportpkg.NewService()
	h := handler.New(store, exportSvc)
	h.RegisterRoutes(app)

	port := os.Getenv("PORT")
	if port == "" {
		port = "3003"
	}

	log.Printf("File Service starting on port %s", port)
	log.Fatal(app.Listen(":" + port))
}
