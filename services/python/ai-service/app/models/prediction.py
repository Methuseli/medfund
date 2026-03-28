from pydantic import BaseModel
from typing import Optional
from datetime import datetime
import uuid


class AIPrediction(BaseModel):
    id: str = ""
    tenant_id: str
    entity_type: str  # claim, fraud, ocr
    entity_id: str
    prediction_type: str  # adjudication, fraud_detection, ocr_extraction
    model_version: str = "1.0.0"
    input_features: dict = {}
    output: dict = {}
    confidence: float
    accepted: Optional[bool] = None
    reviewed_by: Optional[str] = None
    reviewed_at: Optional[datetime] = None
    created_at: datetime = datetime.now(tz=__import__('datetime').timezone.utc)

    def __init__(self, **data):
        super().__init__(**data)
        if not self.id:
            self.id = str(uuid.uuid4())
