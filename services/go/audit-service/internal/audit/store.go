package audit

import (
	"sort"
	"strings"
	"sync"
	"time"
)

type Store struct {
	mu       sync.RWMutex
	events   []Event
	security []SecurityEvent
}

func NewStore() *Store {
	return &Store{
		events:   make([]Event, 0),
		security: make([]SecurityEvent, 0),
	}
}

func (s *Store) Append(event Event) {
	s.mu.Lock()
	defer s.mu.Unlock()
	if event.Timestamp.IsZero() {
		event.Timestamp = time.Now()
	}
	s.events = append(s.events, event)
}

func (s *Store) AppendSecurity(event SecurityEvent) {
	s.mu.Lock()
	defer s.mu.Unlock()
	if event.Timestamp.IsZero() {
		event.Timestamp = time.Now()
	}
	s.security = append(s.security, event)
}

func (s *Store) Query(filter QueryFilter) []Event {
	s.mu.RLock()
	defer s.mu.RUnlock()

	var results []Event
	for _, e := range s.events {
		if filter.TenantID != "" && e.TenantID != filter.TenantID {
			continue
		}
		if filter.EntityType != "" && !strings.EqualFold(e.EntityType, filter.EntityType) {
			continue
		}
		if filter.EntityID != "" && e.EntityID != filter.EntityID {
			continue
		}
		if filter.Action != "" && !strings.EqualFold(e.Action, filter.Action) {
			continue
		}
		if filter.ActorID != "" && e.ActorID != filter.ActorID {
			continue
		}
		if !filter.StartDate.IsZero() && e.Timestamp.Before(filter.StartDate) {
			continue
		}
		if !filter.EndDate.IsZero() && e.Timestamp.After(filter.EndDate) {
			continue
		}
		results = append(results, e)
	}

	// Sort newest first
	sort.Slice(results, func(i, j int) bool {
		return results[i].Timestamp.After(results[j].Timestamp)
	})

	// Paginate
	page := filter.Page
	pageSize := filter.PageSize
	if pageSize <= 0 {
		pageSize = 50
	}
	if page <= 0 {
		page = 1
	}
	start := (page - 1) * pageSize
	if start >= len(results) {
		return nil
	}
	end := start + pageSize
	if end > len(results) {
		end = len(results)
	}

	return results[start:end]
}

func (s *Store) Count() int {
	s.mu.RLock()
	defer s.mu.RUnlock()
	return len(s.events)
}
