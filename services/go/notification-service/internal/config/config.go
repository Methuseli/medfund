package config

import "os"

type Config struct {
	Port            string
	KafkaBrokers    string
	ConsumerGroupID string
	SMTPHost        string
	SMTPPort        string
	SMTPUser        string
	SMTPPassword    string
	SMSProvider     string
	SMSAPIKey       string
	FCMServerKey    string
}

func Load() *Config {
	return &Config{
		Port:            getEnv("PORT", "3001"),
		KafkaBrokers:    getEnv("KAFKA_BROKERS", "localhost:9092"),
		ConsumerGroupID: getEnv("CONSUMER_GROUP_ID", "notification-service"),
		SMTPHost:        getEnv("SMTP_HOST", "localhost"),
		SMTPPort:        getEnv("SMTP_PORT", "587"),
		SMTPUser:        getEnv("SMTP_USER", ""),
		SMTPPassword:    getEnv("SMTP_PASSWORD", ""),
		SMSProvider:     getEnv("SMS_PROVIDER", "mock"),
		SMSAPIKey:       getEnv("SMS_API_KEY", ""),
		FCMServerKey:    getEnv("FCM_SERVER_KEY", ""),
	}
}

func getEnv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}
