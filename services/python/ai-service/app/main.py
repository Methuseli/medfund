"""MedFund AI Service — FastAPI application."""
from contextlib import asynccontextmanager
from fastapi import FastAPI
import logging

from app.core.config import settings
from app.core.anthropic_client import ClaudeClient
import app.core.anthropic_client as anthropic_module
from app.core.database import init_db, close_db

from app.api.health import router as health_router
from app.api.adjudication import router as adjudication_router
from app.api.fraud import router as fraud_router
from app.api.ocr import router as ocr_router
from app.api.chatbot import router as chatbot_router

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application startup and shutdown."""
    logger.info("AI Service starting up...")

    # Initialize Claude client
    anthropic_module.claude_client = ClaudeClient(api_key=settings.anthropic_api_key)
    if anthropic_module.claude_client.available:
        logger.info("Claude AI integration: ACTIVE")
    else:
        logger.info("Claude AI integration: INACTIVE (using rule-based fallbacks)")

    # Initialize database
    try:
        await init_db(settings.database_url)
        logger.info("Database: CONNECTED")
    except Exception as e:
        logger.warning(f"Database initialization failed: {e}. Running without persistence.")

    # Start Kafka consumer (if configured)
    kafka_consumer = None
    if settings.kafka_bootstrap_servers:
        try:
            from app.core.kafka_consumer import ClaimsEventConsumer
            from app.services.adjudication_service import AdjudicationService
            from app.services.fraud_service import FraudService

            adj_svc = AdjudicationService(anthropic_module.claude_client)
            fraud_svc = FraudService()
            kafka_consumer = ClaimsEventConsumer(
                settings.kafka_bootstrap_servers, adj_svc, fraud_svc
            )
            await kafka_consumer.start()
            logger.info("Kafka consumer: STARTED")
        except Exception as e:
            logger.warning(f"Kafka consumer failed to start: {e}")

    yield

    # Shutdown
    if kafka_consumer:
        await kafka_consumer.stop()
    await close_db()
    logger.info("AI Service shut down")


app = FastAPI(
    title="MedFund AI Service",
    version="0.1.0",
    description="AI-powered adjudication, fraud detection, OCR, and chatbot for healthcare claims",
    docs_url="/docs",
    lifespan=lifespan,
)

app.include_router(health_router)
app.include_router(adjudication_router)
app.include_router(fraud_router)
app.include_router(ocr_router)
app.include_router(chatbot_router)
