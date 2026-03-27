from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api import health


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup: initialize DB pool, Kafka producer, ML models
    yield
    # Shutdown: close connections


app = FastAPI(
    title="MedFund AI Service",
    version="0.1.0",
    docs_url="/docs",
    lifespan=lifespan,
)

app.include_router(health.router)
