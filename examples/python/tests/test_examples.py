from __future__ import annotations

from typing import Any

import pytest

from marona_agents_examples import bring_your_own_agent
from marona_agents_examples.basic_agent import build_agent
from marona_agents_examples.multi_agent import build_agent_graph, check_refund
from marona_agents_examples.realtime_agent import build_realtime_agent


class FakeConnection:
    def __init__(self) -> None:
        self.calls: list[tuple[str, dict[str, Any], str | None, str | None]] = []

    def list_tools(self) -> list[dict[str, Any]]:
        return [{"name": "example__lookup", "inputSchema": {"type": "object"}}]

    def call_tool(
        self,
        name: str,
        arguments: dict[str, Any],
        *,
        user_id: str | None = None,
        conversation_id: str | None = None,
    ) -> dict[str, bool]:
        self.calls.append((name, arguments, user_id, conversation_id))
        return {"ok": True}


@pytest.fixture(autouse=True)
def api_key(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setenv("MARONA_API_KEY", "test-key")
    monkeypatch.delenv("MARONA_APP_SLUG", raising=False)


def test_basic_agent_has_provider_neutral_defaults() -> None:
    marona, agent = build_agent()
    try:
        assert agent.name == "Customer Assistant"
        assert agent.model == "marona/auto"
        assert agent.tool_choice == "auto"
    finally:
        marona.close()


def test_multi_agent_graph_contains_delegation_and_handoff() -> None:
    marona, manager = build_agent_graph()
    try:
        assert manager.name == "Customer Support Manager"
        assert len(manager.tools) == 1
        assert len(manager.handoffs) == 1
        assert check_refund.name == "check_refund"
    finally:
        marona.close()


def test_existing_agent_adapter_preserves_identity_context() -> None:
    connection = FakeConnection()
    adapter = bring_your_own_agent.ExistingAgentAdapter(connection)

    assert adapter.available_tools()[0]["name"] == "example__lookup"
    assert adapter.execute_selected_tool(
        "example__lookup",
        {"query": "Marona"},
        user_id="developer-1",
        session_id="session-1",
    ) == {"ok": True}
    assert connection.calls == [
        ("example__lookup", {"query": "Marona"}, "developer-1", "session-1")
    ]


def test_realtime_agent_declares_audio_contract() -> None:
    marona, agent = build_realtime_agent()
    try:
        assert agent.voice["turn_detection"] == "semantic_vad"
        assert agent.modalities == ["text", "audio"]
        assert agent.output_modalities == ["text", "audio"]
    finally:
        marona.close()
