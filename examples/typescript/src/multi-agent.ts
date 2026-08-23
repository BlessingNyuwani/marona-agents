import { Agent, Marona, Runner, functionTool, handoff } from "marona";

import { requiredApiKey, selectedModel } from "./config.js";

export const checkRefund = functionTool({
  name: "check_refund",
  description: "Check whether a duplicate charge is eligible for a refund.",
  inputSchema: {
    type: "object",
    properties: {
      amount: { type: "number" },
      duplicate_charge: { type: "boolean" },
    },
    required: ["amount", "duplicate_charge"],
    additionalProperties: false,
  },
  permissions: ["billing.refund.read"],
  execute: async (input, context) => {
    const amount = Number(input.amount);
    const duplicateCharge = input.duplicate_charge === true;
    return {
      eligible: duplicateCharge && amount > 0,
      refund_amount: duplicateCharge ? amount : 0,
      customer_id: context.userId,
    };
  },
});

export function buildAgentGraph(): { marona: Marona; manager: Agent } {
  const marona = new Marona({ apiKey: requiredApiKey() });
  const researcher = new Agent({
    name: "Research Specialist",
    description: "Researches one bounded customer question.",
    model: selectedModel(),
    instructions: "Return a concise factual summary to the manager.",
  });
  const billing = new Agent({
    name: "Billing Specialist",
    description: "Owns billing and payment conversations.",
    model: selectedModel(),
    instructions: "Use validated billing tools before making a decision.",
    tools: [checkRefund],
    metadata: { permissions: ["billing.refund.read"] },
  });
  const manager = new Agent({
    name: "Customer Support Manager",
    model: selectedModel(),
    instructions: "Delegate research and hand off billing requests.",
    tools: [
      researcher.asTool({
        name: "research",
        description: "Research one bounded question and return the result.",
      }),
    ],
    handoffs: [
      handoff(billing, {
        name: "transfer_to_billing",
        description: "Transfer billing and payment requests.",
      }),
    ],
  });
  return { marona, manager };
}

export async function runMultiAgent(): Promise<void> {
  const { manager } = buildAgentGraph();
  const result = await Runner.run(
    manager,
    "I was charged twice for USD 24. Check whether I qualify for a refund.",
    { userId: "example-user", sessionId: "multi-agent-session" },
  );
  console.log(result.output);
  console.log(`Active agent: ${result.activeAgent.name}`);
}
