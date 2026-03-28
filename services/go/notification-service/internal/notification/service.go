package notification

import (
	"encoding/json"
	"log"
)

type Channel string

const (
	ChannelEmail Channel = "email"
	ChannelSMS   Channel = "sms"
	ChannelPush  Channel = "push"
)

type Notification struct {
	TenantID  string            `json:"tenantId"`
	Channel   Channel           `json:"channel"`
	Recipient string            `json:"recipient"`
	Subject   string            `json:"subject"`
	Body      string            `json:"body"`
	Template  string            `json:"template"`
	Data      map[string]string `json:"data"`
}

type Service struct {
	emailSender EmailSender
	smsSender   SMSSender
	pushSender  PushSender
}

func NewService() *Service {
	return &Service{
		emailSender: &MockEmailSender{},
		smsSender:   &MockSMSSender{},
		pushSender:  &MockPushSender{},
	}
}

func (s *Service) Send(n Notification) error {
	log.Printf("[notification] Sending %s to %s: %s", n.Channel, n.Recipient, n.Subject)
	switch n.Channel {
	case ChannelEmail:
		return s.emailSender.Send(n.Recipient, n.Subject, n.Body)
	case ChannelSMS:
		return s.smsSender.Send(n.Recipient, n.Body)
	case ChannelPush:
		return s.pushSender.Send(n.Recipient, n.Subject, n.Body)
	default:
		log.Printf("[notification] Unknown channel: %s", n.Channel)
		return nil
	}
}

func (s *Service) ProcessEvent(topic string, value []byte) {
	log.Printf("[notification] Processing event from topic: %s", topic)

	var event map[string]interface{}
	if err := json.Unmarshal(value, &event); err != nil {
		log.Printf("[notification] Failed to parse event: %v", err)
		return
	}

	eventType, _ := event["event"].(string)

	switch eventType {
	case "CLAIM_ADJUDICATED":
		log.Printf("[notification] Claim adjudicated — would send notification to member")
	case "MEMBER_ENROLLED":
		log.Printf("[notification] Member enrolled — would send welcome email")
	case "PAYMENT_RUN_EXECUTED":
		log.Printf("[notification] Payment run executed — would notify providers")
	case "CONTRIBUTION_PAID":
		log.Printf("[notification] Contribution paid — would send receipt")
	default:
		log.Printf("[notification] Unhandled event type: %s", eventType)
	}
}

// Sender interfaces
type EmailSender interface {
	Send(to, subject, body string) error
}
type SMSSender interface {
	Send(to, body string) error
}
type PushSender interface {
	Send(deviceToken, title, body string) error
}

// Mock implementations
type MockEmailSender struct{}

func (m *MockEmailSender) Send(to, subject, body string) error {
	log.Printf("[email] Mock send to=%s subject=%s", to, subject)
	return nil
}

type MockSMSSender struct{}

func (m *MockSMSSender) Send(to, body string) error {
	log.Printf("[sms] Mock send to=%s", to)
	return nil
}

type MockPushSender struct{}

func (m *MockPushSender) Send(deviceToken, title, body string) error {
	log.Printf("[push] Mock send token=%s title=%s", deviceToken, title)
	return nil
}
