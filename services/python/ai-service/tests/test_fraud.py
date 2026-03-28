from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_fraud_check_low_risk():
    response = client.post(
        "/api/v1/ai/fraud/check",
        json={
            "claim_id": "CLM-001",
            "member_id": "MBR-001",
            "provider_id": "PRV-001",
            "claimed_amount": 200.00,
            "service_date": "2026-03-01",
        },
        headers={"X-Tenant-ID": "test-tenant"},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["claim_id"] == "CLM-001"
    assert data["risk_score"] < 0.5
    assert data["risk_level"] in ["LOW", "MEDIUM"]


def test_fraud_check_high_value():
    response = client.post(
        "/api/v1/ai/fraud/check",
        json={
            "claim_id": "CLM-002",
            "member_id": "MBR-001",
            "provider_id": "PRV-001",
            "claimed_amount": 50000.00,
            "service_date": "2026-03-01",
            "procedure_codes": ["0190", "0191", "0192", "0193", "0194", "0195"],
        },
        headers={"X-Tenant-ID": "test-tenant"},
    )
    assert response.status_code == 200
    data = response.json()
    assert "high_value_claim" in data["indicators"]
    assert "many_procedures" in data["indicators"]
