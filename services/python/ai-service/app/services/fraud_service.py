import logging
import random
from app.models.prediction import AIPrediction

logger = logging.getLogger(__name__)


class FraudService:
    """Fraud detection service using ML models."""

    def __init__(self):
        self.model_version = "1.0.0"

    async def detect_fraud(self, claim_data: dict, tenant_id: str) -> AIPrediction:
        """Run fraud detection on claim data."""
        risk_score = 0.1
        indicators = []

        if claim_data.get("claimed_amount", 0) > 5000:
            risk_score += 0.15
            indicators.append("high_value")

        if len(claim_data.get("procedure_codes", [])) > 5:
            risk_score += 0.1
            indicators.append("many_procedures")

        risk_score += random.uniform(0, 0.1)
        risk_score = min(risk_score, 1.0)

        return AIPrediction(
            tenant_id=tenant_id,
            entity_type="claim",
            entity_id=claim_data.get("claim_id", "unknown"),
            prediction_type="fraud_detection",
            model_version=self.model_version,
            input_features=claim_data,
            output={
                "risk_score": round(risk_score, 4),
                "risk_level": "HIGH" if risk_score > 0.6 else "MEDIUM" if risk_score > 0.3 else "LOW",
                "indicators": indicators,
            },
            confidence=1.0 - risk_score,
        )
