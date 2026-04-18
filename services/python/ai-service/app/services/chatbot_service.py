"""Claude-powered chatbot service with conversation history."""
import logging
import uuid

logger = logging.getLogger(__name__)


class ChatbotService:
    """AI chatbot for member queries with conversation context."""

    def __init__(self, gemini_client=None):
        self.gemini_client = gemini_client
        self._conversations: dict[str, list[dict]] = {}  # in-memory fallback

    async def respond(
        self, message: str, conversation_id: str | None = None,
        context: dict = None, tenant_id: str = ""
    ) -> dict:
        """Generate a response to a member message."""
        conv_id = conversation_id or str(uuid.uuid4())

        # Get or create conversation history
        if conv_id not in self._conversations:
            self._conversations[conv_id] = []
        history = self._conversations[conv_id]

        # Add user message
        history.append({"role": "user", "content": message})

        # Try Claude
        if self.gemini_client and self.gemini_client.available:
            try:
                system_prompt = self._build_system_prompt(context)
                reply = await self.gemini_client.complete(
                    system_prompt=system_prompt,
                    messages=history[-10:],  # last 10 messages for context
                )
                if reply:
                    history.append({"role": "assistant", "content": reply})
                    return {
                        "reply": reply,
                        "conversation_id": conv_id,
                        "source": "ai",
                        "confidence": 0.85,
                    }
            except Exception as e:
                logger.warning(f"Claude chatbot failed: {e}")

        # Fallback
        reply = self._fallback_response(message)
        history.append({"role": "assistant", "content": reply})
        return {
            "reply": reply,
            "conversation_id": conv_id,
            "source": "fallback",
            "confidence": 0.3,
        }

    def _build_system_prompt(self, context: dict | None) -> str:
        prompt = """You are a helpful healthcare benefits assistant for MedFund.
You help members understand their benefits, check claim status, and answer FAQs.
You NEVER modify any data — only provide information and guidance.
Keep responses concise and friendly."""
        if context:
            prompt += f"\n\nMember context: {context}"
        return prompt

    def _fallback_response(self, message: str) -> str:
        msg_lower = message.lower()
        if "balance" in msg_lower or "benefit" in msg_lower:
            return "To check your benefit balance, please visit the Benefits section in your dashboard or contact your scheme administrator."
        if "claim" in msg_lower:
            return "For claim status inquiries, please check the Claims section in your dashboard. If you need further help, contact our support team."
        if "payment" in msg_lower:
            return "For payment information, please check the Payments section in your dashboard."
        return "Thank you for your message. Our AI assistant is available to help with benefits, claims, and payment questions. Please contact support for complex inquiries."
