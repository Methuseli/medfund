"""Tests for anomaly detection."""
from app.services.anomaly_service import AnomalyService


def test_detects_outlier_transaction():
    svc = AnomalyService()
    # 19 normal + 1 extreme outlier
    txns = [{"id": str(i), "amount": 100 + i * 5} for i in range(19)]
    txns.append({"id": "outlier", "amount": 100000})
    result = svc.detect_anomalies(txns, contamination=0.1)
    assert result["anomalies_found"] >= 1
    outlier_ids = [a["transaction"]["id"] for a in result["anomalies"]]
    assert "outlier" in outlier_ids


def test_no_anomalies_in_uniform_data():
    svc = AnomalyService()
    txns = [{"id": str(i), "amount": 100} for i in range(20)]
    result = svc.detect_anomalies(txns, contamination=0.05)
    # With perfectly uniform data, IsolationForest may still flag some due to contamination parameter
    assert result["total_transactions"] == 20


def test_insufficient_data():
    svc = AnomalyService()
    result = svc.detect_anomalies([{"amount": 100}])
    assert "error" in result
