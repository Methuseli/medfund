package main

import (
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
)

func main() {
	app := fiber.New(fiber.Config{
		AppName: "MedFund Audit Service",
	})

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{"status": "ok", "service": "audit-service"})
	})

	port := os.Getenv("PORT")
	if port == "" {
		port = "3002"
	}

	log.Fatal(app.Listen(":" + port))
}
