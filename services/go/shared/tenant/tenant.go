package tenant

import (
	"github.com/gofiber/fiber/v2"
)

const HeaderTenantID = "X-Tenant-ID"
const LocalTenantID = "tenant_id"

// Middleware extracts X-Tenant-ID header and stores in Fiber locals.
func Middleware() fiber.Handler {
	return func(c *fiber.Ctx) error {
		tenantID := c.Get(HeaderTenantID)
		if tenantID == "" {
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
				"error": "tenant not resolved",
			})
		}
		c.Locals(LocalTenantID, tenantID)
		return c.Next()
	}
}

// Get retrieves the tenant ID from Fiber context.
func Get(c *fiber.Ctx) string {
	id, _ := c.Locals(LocalTenantID).(string)
	return id
}
