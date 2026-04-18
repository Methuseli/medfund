"""AI chatbot endpoints."""
from fastapi import APIRouter, Header
from pydantic import BaseModel
from typing import Optional
import logging

from app.services.chatbot_service import ChatbotService
from app.core.gemini_client import gemini_client

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/ai/chat", tags=["AI Chatbot"])

_chatbot = ChatbotService(gemini_client)


class ChatMessage(BaseModel):
    message: str
    conversation_id: Optional[str] = None
    context: dict = {}


class ChatResponse(BaseModel):
    reply: str
    conversation_id: str
    source: str
    confidence: float


@router.post("/message", response_model=ChatResponse)
async def chat(
    request: ChatMessage,
    x_tenant_id: str = Header(..., alias="X-Tenant-ID"),
):
    """AI-powered chatbot for member queries."""
    result = await _chatbot.respond(
        message=request.message,
        conversation_id=request.conversation_id,
        context=request.context,
        tenant_id=x_tenant_id,
    )
    return ChatResponse(**result)
