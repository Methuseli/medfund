package audit

import "time"

type Event struct {
	ID            string                 `json:"id"`
	TenantID      string                 `json:"tenantId"`
	EntityType    string                 `json:"entityType"`
	EntityID      string                 `json:"entityId"`
	Action        string                 `json:"action"`
	ActorID       string                 `json:"actorId"`
	ActorEmail    string                 `json:"actorEmail"`
	OldValue      map[string]interface{} `json:"oldValue"`
	NewValue      map[string]interface{} `json:"newValue"`
	ChangedFields []string               `json:"changedFields"`
	CorrelationID string                 `json:"correlationId"`
	Timestamp     time.Time              `json:"timestamp"`
}

type SecurityEvent struct {
	ID        string    `json:"id"`
	TenantID  string    `json:"tenantId"`
	EventType string    `json:"eventType"` // LOGIN, LOGOUT, FAILED_AUTH, MFA, PASSWORD_CHANGE, etc.
	UserID    string    `json:"userId"`
	IPAddress string    `json:"ipAddress"`
	UserAgent string    `json:"userAgent"`
	Details   string    `json:"details"`
	Timestamp time.Time `json:"timestamp"`
}

type QueryFilter struct {
	TenantID   string
	EntityType string
	EntityID   string
	Action     string
	ActorID    string
	StartDate  time.Time
	EndDate    time.Time
	Page       int
	PageSize   int
}
