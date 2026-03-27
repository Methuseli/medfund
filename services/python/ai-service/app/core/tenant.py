from fastapi import Request
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession


async def get_tenant_session(request: Request, session: AsyncSession) -> AsyncSession:
    """Dependency that sets PostgreSQL search_path to the tenant's schema."""
    tenant_id = request.headers.get("X-Tenant-ID")
    if not tenant_id:
        raise ValueError("X-Tenant-ID header is required")
    schema = f"tenant_{tenant_id.replace('-', '')}"
    await session.execute(text(f"SET search_path TO {schema}, public"))
    return session
