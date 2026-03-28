"""Anthropic Claude client wrapper with graceful fallback."""
import logging
from typing import Optional

logger = logging.getLogger(__name__)


class ClaudeClient:
    """Wrapper around Anthropic's async client. Falls back to None when API key is not set."""

    def __init__(self, api_key: str = ""):
        self._client = None
        self._available = False

        if api_key:
            try:
                import anthropic
                self._client = anthropic.AsyncAnthropic(api_key=api_key)
                self._available = True
                logger.info("Claude client initialized successfully")
            except Exception as e:
                logger.warning(f"Failed to initialize Claude client: {e}")
        else:
            logger.info("Claude client not initialized — MEDFUND_ANTHROPIC_API_KEY not set. Using rule-based fallbacks.")

    @property
    def available(self) -> bool:
        return self._available

    async def complete(
        self,
        system_prompt: str,
        messages: list[dict],
        max_tokens: int = 1024,
        model: str = "claude-sonnet-4-20250514",
    ) -> Optional[str]:
        """Send a completion request to Claude. Returns None if client unavailable."""
        if not self._available:
            return None

        try:
            response = await self._client.messages.create(
                model=model,
                max_tokens=max_tokens,
                system=system_prompt,
                messages=messages,
            )
            return response.content[0].text
        except Exception as e:
            logger.error(f"Claude API call failed: {e}")
            return None

    async def complete_json(
        self,
        system_prompt: str,
        messages: list[dict],
        max_tokens: int = 1024,
    ) -> Optional[dict]:
        """Send a completion request expecting JSON response. Parses and returns dict."""
        import json

        text = await self.complete(
            system_prompt=system_prompt + "\n\nRespond ONLY with valid JSON, no markdown or explanation.",
            messages=messages,
            max_tokens=max_tokens,
        )
        if text is None:
            return None

        try:
            # Strip markdown code fences if present
            cleaned = text.strip()
            if cleaned.startswith("```"):
                cleaned = cleaned.split("\n", 1)[1] if "\n" in cleaned else cleaned
                cleaned = cleaned.rsplit("```", 1)[0]
            return json.loads(cleaned)
        except json.JSONDecodeError:
            logger.warning(f"Failed to parse Claude response as JSON: {text[:200]}")
            return None


# Global instance — initialized in main.py lifespan
claude_client: ClaudeClient = ClaudeClient()
