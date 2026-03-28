from fastapi import APIRouter, Header, HTTPException
from pydantic import BaseModel
from typing import Optional
import logging

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/ai/adjudication", tags=["AI Adjudication"])


class AdjudicationRequest(BaseModel):
    claim_id: str
    member_id: str
    provider_id: str
    diagnosis_codes: list[str] = []
    procedure_codes: list[str] = []
    claimed_amount: float
    currency_code: str = "USD"
    claim_type: str = "medical"
    service_date: str


class AdjudicationRecommendation(BaseModel):
    claim_id: str
    recommendation: str  # APPROVE, REJECT, REVIEW
    confidence: float
    approved_amount: Optional[float] = None
    reasoning: str
    flags: list[str] = []
    model_version: str = "1.0.0"


@router.post("/recommend", response_model=AdjudicationRecommendation)
async def recommend_adjudication(
    request: AdjudicationRequest,
    x_tenant_id: str = Header(..., alias="X-Tenant-ID"),
):
    """AI-assisted adjudication recommendation using rule-based + Claude reasoning."""
    logger.info(f"Adjudication request for claim {request.claim_id}, tenant {x_tenant_id}")

    # Simplified rule-based recommendation (Claude integration comes later)
    flags = []
    confidence = 0.85
    recommendation = "APPROVE"
    approved_amount = request.claimed_amount

    if request.claimed_amount > 10000:
        flags.append("HIGH_VALUE_CLAIM")
        recommendation = "REVIEW"
        confidence = 0.65

    if not request.diagnosis_codes:
        flags.append("MISSING_DIAGNOSIS")
        recommendation = "REVIEW"
        confidence = 0.50

    reasoning = f"Rule-based analysis: {len(request.procedure_codes)} procedures, "
    reasoning += f"{len(request.diagnosis_codes)} diagnoses, amount={request.claimed_amount} {request.currency_code}. "
    if flags:
        reasoning += f"Flags: {', '.join(flags)}."
    else:
        reasoning += "No flags raised."

    return AdjudicationRecommendation(
        claim_id=request.claim_id,
        recommendation=recommendation,
        confidence=confidence,
        approved_amount=approved_amount if recommendation == "APPROVE" else None,
        reasoning=reasoning,
        flags=flags,
    )
