"""Tests for ML fraud detection model."""
from app.services.ml_models import FraudMLModel


def test_model_trains_successfully():
    model = FraudMLModel(seed=42)
    assert model._trained is True


def test_model_predicts_low_risk_for_normal_claim():
    model = FraudMLModel(seed=42)
    result = model.predict({"amount": 300, "procedure_count": 2, "diagnosis_count": 1, "days_since_enrollment": 500})
    assert result["risk_score"] < 0.5
    assert result["risk_level"] in ["LOW", "MEDIUM"]
    assert result["model"] == "isolation_forest"


def test_model_predicts_higher_risk_for_suspicious_claim():
    model = FraudMLModel(seed=42)
    result = model.predict({"amount": 50000, "procedure_count": 8, "diagnosis_count": 0, "days_since_enrollment": 30})
    assert result["risk_score"] > 0.2
    assert "high_value" in result["indicators"]
    assert "many_procedures" in result["indicators"]
    assert "new_member" in result["indicators"]


def test_model_returns_indicators():
    model = FraudMLModel(seed=42)
    result = model.predict({"amount": 100, "procedure_count": 1, "diagnosis_count": 0, "days_since_enrollment": 365})
    assert "no_diagnosis" in result["indicators"]
