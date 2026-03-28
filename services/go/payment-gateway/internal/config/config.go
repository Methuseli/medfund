package config

import "os"

type Config struct {
	Port                string
	PaynowIntegrationID string
	PaynowIntegrationKey string
	StripeSecretKey      string
	WebhookSecret        string
	DatabaseURL          string
}

func Load() *Config {
	return &Config{
		Port:                 getEnv("PORT", "3004"),
		PaynowIntegrationID:  getEnv("PAYNOW_INTEGRATION_ID", ""),
		PaynowIntegrationKey: getEnv("PAYNOW_INTEGRATION_KEY", ""),
		StripeSecretKey:      getEnv("STRIPE_SECRET_KEY", ""),
		WebhookSecret:        getEnv("WEBHOOK_SECRET", ""),
		DatabaseURL:          getEnv("DATABASE_URL", ""),
	}
}

func getEnv(key, fallback string) string {
	if val, ok := os.LookupEnv(key); ok {
		return val
	}
	return fallback
}
