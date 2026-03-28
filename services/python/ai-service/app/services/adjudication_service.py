import logging
from app.models.prediction import AIPrediction

logger = logging.getLogger(__name__)


class AdjudicationService:
    """AI-assisted claims adjudication service."""

    def __init__(self):
        self.model_version = "1.0.0"

    async def analyze_claim(self, claim_data: dict, tenant_id: str) -> AIPrediction:
        """Analyze a claim and produce an AI prediction."""
        logger.info(f"Analyzing claim {claim_data.get('claim_id')} for tenant {tenant_id}")

        confidence = 0.85
        recommendation = "APPROVE"
        flags = []

        amount = claim_data.get("claimed_amount", 0)
        if amount > 10000:
            flags.append("HIGH_VALUE")
            recommendation = "REVIEW"
            confidence = 0.65

        return AIPrediction(
            tenant_id=tenant_id,
            entity_type="claim",
            entity_id=claim_data.get("claim_id", "unknown"),
            prediction_type="adjudication",
            model_version=self.model_version,
            input_features=claim_data,
            output={
                "recommendation": recommendation,
                "confidence": confidence,
                "flags": flags,
            },
            confidence=confidence,
        )
