package middleware

import (
	"strings"

	"github.com/gofiber/fiber/v2"
)

// TenantResolver returns a Fiber middleware that resolves the current tenant from
// multiple sources in priority order:
//  1. JWT claims (set by JWTMiddleware)
//  2. X-Tenant-ID header (service-to-service calls)
//  3. Subdomain extraction (e.g., zmmas.api.medfund.healthcare)
//
// Tenancy and plan endpoints are allowed without a tenant (platform-level).
func TenantResolver() fiber.Handler {
	return func(c *fiber.Ctx) error {
		// Skip for health/swagger
		path := c.Path()
		if strings.HasPrefix(path, "/health") || strings.HasPrefix(path, "/swagger") {
			return c.Next()
		}

		tenantID := ""

		// 1. Check if JWT middleware already set it
		if id, ok := c.Locals("tenant_id").(string); ok && id != "" {
			tenantID = id
		}

		// 2. Check X-Tenant-ID header (service-to-service)
		if tenantID == "" {
			tenantID = c.Get("X-Tenant-ID")
		}

		// 3. Extract from subdomain (e.g., zmmas.api.medfund.healthcare)
		if tenantID == "" {
			host := c.Hostname()
			parts := strings.Split(host, ".")
			if len(parts) > 2 {
				tenantID = parts[0]
			}
		}

		if tenantID == "" {
			// Allow tenancy-service endpoints without tenant (platform-level)
			if strings.HasPrefix(path, "/api/v1/tenants") || strings.HasPrefix(path, "/api/v1/plans") {
				return c.Next()
			}
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
				"error": "tenant could not be resolved",
			})
		}

		c.Locals("tenant_id", tenantID)
		c.Request().Header.Set("X-Tenant-ID", tenantID)
		return c.Next()
	}
}
