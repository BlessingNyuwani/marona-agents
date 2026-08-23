"""Keep an existing orchestrator while Marona governs capabilities."""

from __future__ import annotations

from dataclasses import dataclass
from typing import Any, Protocol

from marona import Marona

from .config import required_api_key, selected_app


class CapabilityConnection(Protocol):
    """Small boundary an existing framework needs from Marona."""

    def list_tools(self) -> list[dict[str, Any]]: ...

    def call_tool(
        self,
        name: str,
        arguments: dict[str, Any],
        *,
        user_id: str | None = None,
        conversation_id: str | None = None,
    ) -> Any: ...


@dataclass(slots=True)
class ExistingAgentAdapter:
    """Example adapter owned by an external agent framework."""

    connection: CapabilityConnection

    def available_tools(self) -> list[dict[str, Any]]:
        return self.connection.list_tools()

    def execute_selected_tool(
        self,
        name: str,
        arguments: dict[str, Any],
        *,
        user_id: str,
        session_id: str,
    ) -> Any:
        return self.connection.call_tool(
            name,
            arguments,
            user_id=user_id,
            conversation_id=session_id,
        )


def main() -> None:
    marona = Marona(api_key=required_api_key())
    try:
        app = selected_app()
        connection = marona.hub.connect(apps=[app] if app else None)
        external_agent = ExistingAgentAdapter(connection)

        print("The external framework can map these neutral tools:")
        for tool in external_agent.available_tools():
            print(f"- {tool.get('name', 'unnamed_tool')}")
        print(
            "Tool selection remains in the external framework; "
            "execution remains governed by Marona."
        )
    finally:
        marona.close()


if __name__ == "__main__":
    main()
