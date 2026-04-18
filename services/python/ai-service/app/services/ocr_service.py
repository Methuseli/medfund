"""Document OCR service — Tesseract text extraction + Claude structured data."""
import logging
import base64
from typing import Optional

logger = logging.getLogger(__name__)


class OCRService:
    """Extracts text from documents using Tesseract, structures data with Claude."""

    def __init__(self, gemini_client=None):
        self.gemini_client = gemini_client
        self._tesseract_available = self._check_tesseract()

    def _check_tesseract(self) -> bool:
        try:
            import pytesseract
            pytesseract.get_tesseract_version()
            return True
        except Exception:
            logger.warning("Tesseract not available — OCR will return empty text")
            return False

    def extract_text(self, image_bytes: bytes) -> str:
        """Extract raw text from image using Tesseract."""
        if not self._tesseract_available:
            return ""
        try:
            import pytesseract
            from PIL import Image
            import io
            image = Image.open(io.BytesIO(image_bytes))
            return pytesseract.image_to_string(image)
        except Exception as e:
            logger.error(f"Tesseract OCR failed: {e}")
            return ""

    async def extract_structured_data(
        self, raw_text: str, image_bytes: bytes | None = None
    ) -> dict:
        """Extract structured claim data from OCR text using Claude."""
        if self.gemini_client and self.gemini_client.available:
            try:
                prompt = f"""Extract structured healthcare claim data from this OCR text.
Return JSON with fields: provider_name, member_id, diagnosis_codes (list),
tariff_codes (list), amounts (list of {{code, amount}}), service_date, currency.
If a field cannot be determined, use null.

OCR Text:
{raw_text}"""
                result = await self.gemini_client.complete_json(
                    system_prompt="You are a healthcare document data extractor.",
                    messages=[{"role": "user", "content": prompt}],
                )
                if result:
                    return result
            except Exception as e:
                logger.warning(f"Claude structured extraction failed: {e}")

        # Fallback: return raw text only
        return {"raw_text": raw_text, "extraction_method": "tesseract_only"}
