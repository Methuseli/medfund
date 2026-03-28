from fastapi import APIRouter, Header, UploadFile, File
from pydantic import BaseModel
from typing import Optional
import logging

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/ai/ocr", tags=["Document OCR"])


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
    """Extract text and structured data from uploaded documents (invoices, claims forms)."""
    logger.info(f"OCR extraction for {file.filename}, tenant {x_tenant_id}")

    content = await file.read()
    file_size = len(content)

    # Stub OCR — real implementation would use Tesseract + Claude Vision
    return OCRResult(
        filename=file.filename or "unknown",
        extracted_text=f"[Stub] OCR extraction placeholder for {file_size} byte document",
        structured_data={
            "document_type": "unknown",
            "file_size_bytes": file_size,
        },
        confidence=0.0,
    )
