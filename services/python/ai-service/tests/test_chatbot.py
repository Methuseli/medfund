from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_chat_message():
    response = client.post(
        "/api/v1/ai/chat/message",
        json={"message": "What is my benefit balance?"},
        headers={"X-Tenant-ID": "test-tenant"},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["source"] == "ai"
    assert data["conversation_id"] is not None
    assert len(data["reply"]) > 0


def test_chat_with_conversation_id():
    response = client.post(
        "/api/v1/ai/chat/message",
        json={
            "message": "Follow up question",
            "conversation_id": "conv-123",
        },
        headers={"X-Tenant-ID": "test-tenant"},
    )
    assert response.status_code == 200
    assert response.json()["conversation_id"] == "conv-123"
