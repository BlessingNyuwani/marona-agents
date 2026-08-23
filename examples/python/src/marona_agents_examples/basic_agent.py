"""A minimal managed Marona Agent."""

from __future__ import annotations

import asyncio

from marona import Agent, Marona, Runner

from .config import required_api_key, selected_app, selected_model


def build_agent() -> tuple[Marona, Agent]:
    """Create the runtime and Agent without making a model request."""

    marona = Marona(api_key=required_api_key())
    app = selected_app()
    tools = marona.hub.connect(apps=[app]) if app else None
    agent = Agent(
        name="Customer Assistant",
        model=selected_model(),
        instructions=(
            "Answer clearly and concisely. Use connected capabilities only when they are needed."
        ),
        tools=tools,
        tool_choice="auto",
    )
    return marona, agent


async def main() -> None:
    marona, agent = build_agent()
    try:
        result = await Runner.run(
            agent,
            "Explain what the Marona AI Runtime does in three sentences.",
            user_id="example-user",
            session_id="basic-agent-session",
        )
        print(result.output)
    finally:
        await marona.aclose()


if __name__ == "__main__":
    asyncio.run(main())
