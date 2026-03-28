package middleware

import (
	"sync"
	"time"

	"github.com/gofiber/fiber/v2"
)

// rateLimiter implements a sliding-window rate limiter keyed by tenant + IP.
type rateLimiter struct {
	mu       sync.Mutex
	requests map[string][]time.Time
	limit    int
	window   time.Duration
}

// NewRateLimiter returns a Fiber middleware that limits requests per tenant+IP
// to the specified number per minute. A background goroutine cleans up expired
// entries every 5 minutes.
func NewRateLimiter(limitPerMinute int) fiber.Handler {
	rl := &rateLimiter{
		requests: make(map[string][]time.Time),
		limit:    limitPerMinute,
		window:   time.Minute,
	}

	// Cleanup goroutine
	go func() {
		for {
			time.Sleep(5 * time.Minute)
			rl.cleanup()
		}
	}()

	return func(c *fiber.Ctx) error {
		tenantID, _ := c.Locals("tenant_id").(string)
		key := tenantID + ":" + c.IP()

		rl.mu.Lock()
		now := time.Now()
		cutoff := now.Add(-rl.window)

		// Filter old requests
		var recent []time.Time
		for _, t := range rl.requests[key] {
			if t.After(cutoff) {
				recent = append(recent, t)
			}
		}

		if len(recent) >= rl.limit {
			rl.mu.Unlock()
			return c.Status(fiber.StatusTooManyRequests).JSON(fiber.Map{
				"error": "rate limit exceeded",
			})
		}

		rl.requests[key] = append(recent, now)
		rl.mu.Unlock()

		return c.Next()
	}
}

// cleanup removes expired entries from the rate limiter map.
func (rl *rateLimiter) cleanup() {
	rl.mu.Lock()
	defer rl.mu.Unlock()
	cutoff := time.Now().Add(-rl.window)
	for key, times := range rl.requests {
		var recent []time.Time
		for _, t := range times {
			if t.After(cutoff) {
				recent = append(recent, t)
			}
		}
		if len(recent) == 0 {
			delete(rl.requests, key)
		} else {
			rl.requests[key] = recent
		}
	}
}
