package main

import (
	"log"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"

	"github.com/medfund/gateway/internal/config"
	"github.com/medfund/gateway/internal/middleware"
	"github.com/medfund/gateway/internal/routes"
)

func main() {
	cfg := config.Load()

	app := fiber.New(fiber.Config{
		AppName:      "MedFund API Gateway",
		ServerHeader: "MedFund",
	})

	// Global middleware
	app.Use(recover.New())
	app.Use(logger.New(logger.Config{
		Format: `{"time":"${time}","status":${status},"method":"${method}","path":"${path}","latency":"${latency}","ip":"${ip}"}` + "\n",
	}))
	app.Use(cors.New(cors.Config{
		AllowOrigins:  "*",
		AllowMethods:  "GET,POST,PUT,PATCH,DELETE,OPTIONS",
		AllowHeaders:  "Origin,Content-Type,Accept,Authorization,X-Tenant-ID",
	}))

	// Health check (no auth required)
	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"status":  "ok",
			"service": "gateway",
		})
	})

	// JWT validation
	jwtMw := middleware.NewJWTMiddleware(cfg.KeycloakURL, cfg.KeycloakRealm)
	app.Use(jwtMw.Handler())

	// Tenant resolution
	app.Use(middleware.TenantResolver())

	// Rate limiting
	app.Use(middleware.NewRateLimiter(cfg.RateLimitPerMin))

	// Service routes
	routes.Register(app, cfg)

	log.Printf("MedFund API Gateway starting on port %s", cfg.Port)
	log.Fatal(app.Listen(":" + cfg.Port))
}
