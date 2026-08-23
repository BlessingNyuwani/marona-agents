"""Command dispatcher for the Python examples."""

from __future__ import annotations

import argparse
import asyncio

from . import basic_agent, bring_your_own_agent, chat_server, multi_agent, realtime_agent


def main() -> None:
    parser = argparse.ArgumentParser(description="Run a Marona Agent Python example.")
    parser.add_argument(
        "example",
        choices=(
            "basic-agent",
            "multi-agent",
            "bring-your-own-agent",
            "realtime-agent",
            "chat-server",
        ),
    )
    selected = parser.parse_args().example
    if selected == "basic-agent":
        asyncio.run(basic_agent.main())
    elif selected == "multi-agent":
        asyncio.run(multi_agent.main())
    elif selected == "bring-your-own-agent":
        bring_your_own_agent.main()
    elif selected == "realtime-agent":
        asyncio.run(realtime_agent.main())
    else:
        chat_server.main()


if __name__ == "__main__":
    main()
