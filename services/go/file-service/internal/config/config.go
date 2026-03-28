package config

import "os"

type Config struct {
	Port           string
	S3Bucket       string
	S3Region       string
	AWSAccessKey   string
	AWSSecretKey   string
	MaxUploadSizeMB string
}

func Load() *Config {
	return &Config{
		Port:            getEnv("PORT", "3003"),
		S3Bucket:        getEnv("S3_BUCKET", ""),
		S3Region:        getEnv("S3_REGION", "us-east-1"),
		AWSAccessKey:    getEnv("AWS_ACCESS_KEY", ""),
		AWSSecretKey:    getEnv("AWS_SECRET_KEY", ""),
		MaxUploadSizeMB: getEnv("MAX_UPLOAD_SIZE_MB", "50"),
	}
}

func getEnv(key, fallback string) string {
	if val, ok := os.LookupEnv(key); ok {
		return val
	}
	return fallback
}
