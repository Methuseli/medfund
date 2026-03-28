from contextlib import asynccontextmanager
from fastapi import FastAPI
import logging

from app.api.health import router as health_router
from app.api.adjudication import router as adjudication_router
from app.api.fraud import router as fraud_router
from app.api.ocr import router as ocr_router
from app.api.chatbot import router as chatbot_router

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("AI Service starting up...")
    # TODO: Initialize database connections, Kafka consumers, ML models
    yield
    logger.info("AI Service shutting down...")


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
