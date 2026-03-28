package storage

import (
	"fmt"
	"time"
)

type PresignedURL struct {
	URL       string    `json:"url"`
	ExpiresAt time.Time `json:"expiresAt"`
	Key       string    `json:"key"`
}

type Storage interface {
	GenerateUploadURL(tenantID, filename, contentType string) (*PresignedURL, error)
	GenerateDownloadURL(tenantID, key string) (*PresignedURL, error)
	Delete(tenantID, key string) error
}

// MockStorage for development
type MockStorage struct{}

func NewMockStorage() *MockStorage { return &MockStorage{} }

func (m *MockStorage) GenerateUploadURL(tenantID, filename, contentType string) (*PresignedURL, error) {
	key := fmt.Sprintf("%s/%d-%s", tenantID, time.Now().UnixNano(), filename)
	return &PresignedURL{
		URL:       fmt.Sprintf("https://mock-s3.example.com/upload/%s", key),
		ExpiresAt: time.Now().Add(15 * time.Minute),
		Key:       key,
	}, nil
}

func (m *MockStorage) GenerateDownloadURL(tenantID, key string) (*PresignedURL, error) {
	return &PresignedURL{
		URL:       fmt.Sprintf("https://mock-s3.example.com/download/%s", key),
		ExpiresAt: time.Now().Add(1 * time.Hour),
		Key:       key,
	}, nil
}

func (m *MockStorage) Delete(tenantID, key string) error { return nil }
