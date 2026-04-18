"""Document OCR endpoints."""
from fastapi import APIRouter, Header, UploadFile, File
from pydantic import BaseModel
import logging

from app.services.ocr_service import OCRService
from app.core.gemini_client import gemini_client

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/ai/ocr", tags=["Document OCR"])

_ocr_service = OCRService(gemini_client)


class OCRResult(BaseModel):
    filename: str
    extracted_text: str
    structured_data: dict
    confidence: float
    model_version: str = "1.0.0"


@router.post("/extract", response_model=OCRResult)
async def extract_document(
    file: UploadFile = File(...),
    x_tenant_id: str = Header(..., alias="X-Tenant-ID"),
):
    """Extract text and structured data from uploaded documents."""
    logger.info(f"OCR extraction for {file.filename}, tenant {x_tenant_id}")

    content = await file.read()
    raw_text = _ocr_service.extract_text(content)
    structured = await _ocr_service.extract_structured_data(raw_text, content)

    confidence = 0.8 if raw_text else 0.0
    if structured.get("extraction_method") == "tesseract_only":
        confidence = 0.4

    return OCRResult(
        filename=file.filename or "unknown",
        extracted_text=raw_text,
        structured_data=structured,
        confidence=confidence,
    )
