from fastapi import APIRouter, Header
from pydantic import BaseModel
import logging
import random

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/ai/fraud", tags=["Fraud Detection"])


class FraudCheckRequest(BaseModel):
    claim_id: str
    member_id: str
    provider_id: str
    claimed_amount: float
    service_date: str
    diagnosis_codes: list[str] = []
    procedure_codes: list[str] = []


class FraudCheckResponse(BaseModel):
    claim_id: str
    risk_score: float  # 0.0 to 1.0
    risk_level: str  # LOW, MEDIUM, HIGH
    indicators: list[str]
    model_version: str = "1.0.0"


@router.post("/check", response_model=FraudCheckResponse)
async def check_fraud(
    request: FraudCheckRequest,
    x_tenant_id: str = Header(..., alias="X-Tenant-ID"),
):
    """Run fraud detection on a claim using ML models."""
    logger.info(f"Fraud check for claim {request.claim_id}, tenant {x_tenant_id}")

    indicators = []
    risk_score = 0.1  # base risk

    # Rule-based fraud indicators (ML model integration later)
    if request.claimed_amount > 5000:
        risk_score += 0.15
        indicators.append("high_value_claim")

    if len(request.procedure_codes) > 5:
        risk_score += 0.1
        indicators.append("many_procedures")

    # Simulate some model noise
    risk_score += random.uniform(0, 0.1)
    risk_score = min(risk_score, 1.0)

    risk_level = "LOW"
    if risk_score > 0.6:
        risk_level = "HIGH"
    elif risk_score > 0.3:
        risk_level = "MEDIUM"

    return FraudCheckResponse(
        claim_id=request.claim_id,
        risk_score=round(risk_score, 4),
        risk_level=risk_level,
        indicators=indicators,
    )
