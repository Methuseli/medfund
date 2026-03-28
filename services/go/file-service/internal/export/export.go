package export

import "log"

type Service struct{}

func NewService() *Service { return &Service{} }

func (s *Service) GeneratePDF(tenantID, templateType string, data map[string]interface{}) ([]byte, error) {
	log.Printf("[export] PDF generation stub: tenant=%s template=%s", tenantID, templateType)
	// Stub — return minimal PDF-like content
	return []byte("%PDF-1.4 stub"), nil
}

func (s *Service) GenerateCSV(tenantID string, headers []string, rows [][]string) ([]byte, error) {
	log.Printf("[export] CSV generation: tenant=%s headers=%v rows=%d", tenantID, headers, len(rows))
	var result string
	for i, h := range headers {
		if i > 0 {
			result += ","
		}
		result += h
	}
	result += "\n"
	for _, row := range rows {
		for i, cell := range row {
			if i > 0 {
				result += ","
			}
			result += cell
		}
		result += "\n"
	}
	return []byte(result), nil
}
