package main

import (
	"context"
	"log"
	"os"
	"os/signal"
	"syscall"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"

	"github.com/medfund/audit-service/internal/audit"
	"github.com/medfund/audit-service/internal/consumer"
	"github.com/medfund/audit-service/internal/handler"
)

func main() {
	port := getEnv("PORT", "3002")
	kafkaBrokers := getEnv("KAFKA_BROKERS", "localhost:9092")

	store := audit.NewStore()

	// Start Kafka consumer (non-blocking — runs in background goroutines)
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	c := consumer.New(kafkaBrokers, store)
	c.Start(ctx)

	// HTTP server
	app := fiber.New(fiber.Config{
		AppName: "MedFund Audit Service",
	})

	app.Use(recover.New())
	app.Use(logger.New())

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{"status": "ok", "service": "audit-service"})
	})

	h := handler.New(store)
	h.RegisterRoutes(app)

	// Graceful shutdown
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		<-quit
		log.Println("[audit-service] Shutting down...")
		cancel()
		_ = app.Shutdown()
	}()

	log.Printf("[audit-service] Starting on port %s, Kafka: %s", port, kafkaBrokers)
	if err := app.Listen(":" + port); err != nil {
		log.Fatalf("[audit-service] Server error: %v", err)
	}
}

func getEnv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}
