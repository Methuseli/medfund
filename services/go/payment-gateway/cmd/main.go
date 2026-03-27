package main

import (
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
)

func main() {
	app := fiber.New(fiber.Config{
		AppName: "MedFund Payment Gateway",
	})

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{"status": "ok", "service": "payment-gateway"})
	})

	port := os.Getenv("PORT")
	if port == "" {
		port = "3004"
	}

	log.Fatal(app.Listen(":" + port))
}
