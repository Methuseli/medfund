from fastapi import APIRouter, Header
from pydantic import BaseModel
import logging

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/ai/chat", tags=["AI Chatbot"])


class ChatMessage(BaseModel):
    message: str
    conversation_id: Optional[str] = None
    context: dict = {}

from typing import Optional


class ChatResponse(BaseModel):
    reply: str
    conversation_id: str
    source: str  # ai, faq, escalated
    confidence: float


@router.post("/message", response_model=ChatResponse)
async def chat(
    request: ChatMessage,
    x_tenant_id: str = Header(..., alias="X-Tenant-ID"),
):
    """AI-powered chatbot for member queries — Claude integration."""
    logger.info(f"Chat message from tenant {x_tenant_id}: {request.message[:50]}...")

    import uuid
    conversation_id = request.conversation_id or str(uuid.uuid4())

    # Stub response — Claude integration later
    reply = f"Thank you for your message. I understand you're asking about: '{request.message}'. "
    reply += "Our AI assistant is being set up. Please contact support for immediate help."

    return ChatResponse(
        reply=reply,
        conversation_id=conversation_id,
        source="ai",
        confidence=0.5,
    )
