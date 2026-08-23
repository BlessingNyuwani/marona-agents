"""A persistent provider-neutral realtime session."""

from __future__ import annotations

import asyncio

from marona import Agent, Marona, RealtimeRunner

from .config import required_api_key, selected_model


def build_realtime_agent() -> tuple[Marona, Agent]:
    marona = Marona(api_key=required_api_key())
    agent = Agent(
        name="Realtime Assistant",
        model=selected_model(),
        instructions="Respond naturally and keep spoken answers concise.",
        voice={
            "output_voice": "ash",
            "language": "en",
            "turn_detection": "semantic_vad",
            "interruptible": True,
            "input_format": "pcm16",
            "output_format": "pcm16",
        },
        modalities=["text", "audio"],
        output_modalities=["text", "audio"],
    )
    return marona, agent


async def main() -> None:
    marona, agent = build_realtime_agent()
    try:
        session = await RealtimeRunner.connect(
            agent,
            user_id="example-user",
            session_id="realtime-agent-session",
        )
        async with session:
            await session.send_message("Introduce yourself briefly.")
            async for event in session:
                if event.type == "transcript.delta" and event.delta:
                    print(event.delta, end="", flush=True)
                elif event.type == "response.completed":
                    print()
                    break
    finally:
        await marona.aclose()


if __name__ == "__main__":
    asyncio.run(main())
