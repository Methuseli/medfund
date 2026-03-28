"""Kafka consumer for processing claims events."""
import asyncio
import json
import logging

logger = logging.getLogger(__name__)


class ClaimsEventConsumer:
    """Consumes claims events from Kafka and triggers AI processing."""

    def __init__(self, bootstrap_servers: str, adjudication_service, fraud_service):
        self.bootstrap_servers = bootstrap_servers
        self.adjudication_service = adjudication_service
        self.fraud_service = fraud_service
        self._task = None
        self._running = False

    async def start(self):
        """Start consuming events."""
        if not self.bootstrap_servers:
            logger.info("Kafka bootstrap servers not configured, skipping consumer start")
            return

        self._running = True
        self._task = asyncio.create_task(self._consume())
        logger.info("Kafka consumer started")

    async def stop(self):
        """Stop consuming events."""
        self._running = False
        if self._task:
            self._task.cancel()
            try:
                await self._task
            except asyncio.CancelledError:
                pass
        logger.info("Kafka consumer stopped")

    async def _consume(self):
        """Main consumer loop."""
        try:
            from aiokafka import AIOKafkaConsumer

            consumer = AIOKafkaConsumer(
                "medfund.claims.submitted",
                bootstrap_servers=self.bootstrap_servers,
                group_id="ai-service",
                auto_offset_reset="earliest",
                value_deserializer=lambda m: json.loads(m.decode("utf-8")),
            )
            await consumer.start()
            logger.info("Kafka consumer connected")

            try:
                async for msg in consumer:
                    if not self._running:
                        break
                    await self.process_event(msg.value)
            finally:
                await consumer.stop()
        except Exception as e:
            logger.error(f"Kafka consumer error: {e}")
            if self._running:
                await asyncio.sleep(5)
                asyncio.create_task(self._consume())

    async def process_event(self, event: dict):
        """Process a single claims event."""
        try:
            event_type = event.get("event", "")
            claim_id = event.get("claimId", "")
            tenant_id = event.get("tenantId", "unknown")

            logger.info(f"Processing event: {event_type} for claim {claim_id}")

            if event_type == "CLAIM_SUBMITTED":
                claim_data = {
                    "claim_id": claim_id,
                    "member_id": event.get("memberId", ""),
                    "claimed_amount": float(event.get("claimedAmount", 0)),
                }

                # Run adjudication and fraud detection in parallel
                adjudication_result = await self.adjudication_service.analyze_claim(
                    claim_data, tenant_id
                )
                fraud_result = await self.fraud_service.detect_fraud(claim_data, tenant_id)

                logger.info(
                    f"AI processing complete for claim {claim_id}: "
                    f"adjudication={adjudication_result.output.get('recommendation')}, "
                    f"fraud_risk={fraud_result.output.get('risk_level')}"
                )
        except Exception as e:
            logger.error(f"Failed to process event: {e}")
