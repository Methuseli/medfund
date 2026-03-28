package notification

import (
	"testing"
)

func TestNewService_Creates(t *testing.T) {
	svc := NewService()
	if svc == nil {
		t.Fatal("expected non-nil service")
	}
}

func TestService_Send_Email(t *testing.T) {
	svc := NewService()
	err := svc.Send(Notification{
		TenantID:  "test",
		Channel:   ChannelEmail,
		Recipient: "user@example.com",
		Subject:   "Test",
		Body:      "Hello",
	})
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
}

func TestService_Send_SMS(t *testing.T) {
	svc := NewService()
	err := svc.Send(Notification{
		TenantID:  "test",
		Channel:   ChannelSMS,
		Recipient: "+1234567890",
		Body:      "Test SMS",
	})
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
}

func TestService_Send_Push(t *testing.T) {
	svc := NewService()
	err := svc.Send(Notification{
		TenantID:  "test",
		Channel:   ChannelPush,
		Recipient: "device-token",
		Subject:   "Alert",
		Body:      "Test push",
	})
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
}

func TestService_Send_UnknownChannel(t *testing.T) {
	svc := NewService()
	err := svc.Send(Notification{
		TenantID:  "test",
		Channel:   "carrier_pigeon",
		Recipient: "the sky",
		Body:      "coo",
	})
	if err != nil {
		t.Fatalf("expected no error for unknown channel, got %v", err)
	}
}

func TestService_ProcessEvent_UnknownType(t *testing.T) {
	svc := NewService()
	// Should not panic
	svc.ProcessEvent("test-topic", []byte(`{"event":"UNKNOWN_EVENT"}`))
}

func TestService_ProcessEvent_InvalidJSON(t *testing.T) {
	svc := NewService()
	// Should not panic
	svc.ProcessEvent("test-topic", []byte("not json"))
}
