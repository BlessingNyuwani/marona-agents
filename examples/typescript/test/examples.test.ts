import assert from "node:assert/strict";
import test from "node:test";

import type { MCPTool } from "marona";

import { buildAgent } from "../src/basic-agent.js";
import { ExistingAgentAdapter, type CapabilityConnection } from "../src/bring-your-own-agent.js";
import { buildAgentGraph, checkRefund } from "../src/multi-agent.js";
import { buildRealtimeAgent } from "../src/realtime-agent.js";

process.env.MARONA_API_KEY = "test-key";
delete process.env.MARONA_APP_SLUG;

test("basic Agent uses provider-neutral defaults", async () => {
  const { agent } = await buildAgent();
  assert.equal(agent.name, "Customer Assistant");
  assert.equal(agent.model, "marona/auto");
  assert.equal(agent.toolChoice, "auto");
});

test("multi-Agent graph contains delegation and handoff", () => {
  const { manager } = buildAgentGraph();
  assert.equal(manager.name, "Customer Support Manager");
  assert.equal(Array.isArray(manager.tools) && manager.tools.length, 1);
  assert.equal(manager.handoffs.length, 1);
  assert.equal(checkRefund.name, "check_refund");
});

test("existing Agent adapter preserves identity context", async () => {
  const calls: unknown[] = [];
  const tools: MCPTool[] = [
    {
      name: "example__lookup",
      description: "Lookup",
      inputSchema: { type: "object" },
      annotations: {},
    },
  ];
  const connection: CapabilityConnection = {
    listTools: () => tools,
    callTool: async (name, input, options) => {
      calls.push({ name, input, options });
      return { ok: true };
    },
  };
  const adapter = new ExistingAgentAdapter(connection);
  assert.equal(adapter.availableTools()[0]?.name, "example__lookup");
  assert.deepEqual(
    await adapter.executeSelectedTool(
      "example__lookup",
      { query: "Marona" },
      { userId: "developer-1", sessionId: "session-1" },
    ),
    { ok: true },
  );
  assert.deepEqual(calls, [
    {
      name: "example__lookup",
      input: { query: "Marona" },
      options: { userId: "developer-1", conversationId: "session-1" },
    },
  ]);
});

test("realtime Agent declares audio contract", () => {
  const { agent } = buildRealtimeAgent();
  assert.equal(agent.voice?.turn_detection, "semantic_vad");
  assert.deepEqual(agent.modalities, ["text", "audio"]);
  assert.deepEqual(agent.outputModalities, ["text", "audio"]);
});
