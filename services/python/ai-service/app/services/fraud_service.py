"""Fraud detection service using ML models with Claude explanation."""
import logging
from app.models.prediction import AIPrediction
from app.services.ml_models import FraudMLModel

logger = logging.getLogger(__name__)

# Global model instance — trained once on import
_fraud_model = FraudMLModel()


class FraudService:
    """Fraud detection using IsolationForest ML model."""

    def __init__(self, model: FraudMLModel | None = None):
        self.model = model or _fraud_model
        self.model_version = self.model.model_version

    async def detect_fraud(self, claim_data: dict, tenant_id: str) -> AIPrediction:
        """Run ML-based fraud detection on claim data."""
        logger.info(f"Fraud check for claim {claim_data.get('claim_id')} tenant {tenant_id}")

        features = {
            "amount": float(claim_data.get("claimed_amount", 0)),
            "procedure_count": len(claim_data.get("procedure_codes", [])),
            "diagnosis_count": len(claim_data.get("diagnosis_codes", [])),
            "days_since_enrollment": claim_data.get("days_since_enrollment", 365),
        }

        result = self.model.predict(features)

        return AIPrediction(
            tenant_id=tenant_id,
            entity_type="claim",
            entity_id=claim_data.get("claim_id", "unknown"),
            prediction_type="fraud_detection",
            model_version=self.model_version,
            input_features=claim_data,
            output=result,
            confidence=1.0 - result["risk_score"],
        )
