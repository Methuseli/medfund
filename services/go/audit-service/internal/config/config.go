package config

import "os"

type Config struct {
	Port            string
	KafkaBrokers    string
	ConsumerGroupID string
	DatabaseURL     string
}

func Load() *Config {
	return &Config{
		Port:            getEnv("PORT", "3002"),
		KafkaBrokers:    getEnv("KAFKA_BROKERS", "localhost:9092"),
		ConsumerGroupID: getEnv("CONSUMER_GROUP_ID", "audit-service"),
		DatabaseURL:     getEnv("DATABASE_URL", "postgres://medfund:medfund@localhost:5432/medfund_audit?sslmode=disable"),
	}
}

func getEnv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}
