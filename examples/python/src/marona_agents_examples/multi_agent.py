"""Delegation, handoff, and a validated local function tool."""

from __future__ import annotations

import asyncio
from typing import Any

from marona import Agent, Marona, Runner, ToolContext, function_tool, handoff

from .config import required_api_key, selected_model


@function_tool(permissions=["billing.refund.read"], timeout_ms=5_000)  # type: ignore[untyped-decorator]
def check_refund(
    amount: float,
    duplicate_charge: bool,
    context: ToolContext,
) -> dict[str, Any]:
    """Check whether a duplicate charge is eligible for a refund."""

    return {
        "eligible": duplicate_charge and amount > 0,
        "refund_amount": amount if duplicate_charge else 0,
        "customer_id": context.user_id,
    }


def build_agent_graph() -> tuple[Marona, Agent]:
    """Build a bounded graph without executing it."""

    marona = Marona(api_key=required_api_key())
    researcher = Agent(
        name="Research Specialist",
        description="Researches one bounded customer question.",
        model=selected_model(),
        instructions="Return a concise factual summary to the manager.",
    )
    billing = Agent(
        name="Billing Specialist",
        description="Owns billing and payment conversations.",
        model=selected_model(),
        instructions="Use validated billing tools before making a decision.",
        tools=[check_refund],
        metadata={"permissions": ["billing.refund.read"]},
    )
    manager = Agent(
        name="Customer Support Manager",
        model=selected_model(),
        instructions="Delegate research and hand off billing requests.",
        tools=[
            researcher.as_tool(
                name="research",
                description="Research one bounded question and return the result.",
            )
        ],
        handoffs=[
            handoff(
                billing,
                name="transfer_to_billing",
                description="Transfer billing and payment requests.",
            )
        ],
    )
    return marona, manager


async def main() -> None:
    marona, manager = build_agent_graph()
    try:
        result = await Runner.run(
            manager,
            "I was charged twice for USD 24. Check whether I qualify for a refund.",
            user_id="example-user",
            session_id="multi-agent-session",
        )
        print(result.output)
        print(f"Active agent: {result.active_agent.name}")
    finally:
        await marona.aclose()


if __name__ == "__main__":
    asyncio.run(main())
