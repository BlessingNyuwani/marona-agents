import { Marona, type MCPTool } from "marona";

import { requiredApiKey, selectedApp } from "./config.js";

export interface CapabilityConnection {
  listTools(): MCPTool[];
  callTool(
    name: string,
    input: Record<string, unknown>,
    options?: { userId?: string; conversationId?: string },
  ): Promise<Record<string, unknown>>;
}

export class ExistingAgentAdapter {
  constructor(private readonly connection: CapabilityConnection) {}

  availableTools(): MCPTool[] {
    return this.connection.listTools();
  }

  executeSelectedTool(
    name: string,
    input: Record<string, unknown>,
    identity: { userId: string; sessionId: string },
  ): Promise<Record<string, unknown>> {
    return this.connection.callTool(name, input, {
      userId: identity.userId,
      conversationId: identity.sessionId,
    });
  }
}

export async function runBringYourOwnAgent(): Promise<void> {
  const marona = new Marona({ apiKey: requiredApiKey() });
  const app = selectedApp();
  const connection = await marona.hub.connect(app ? { apps: [app] } : {});
  const externalAgent = new ExistingAgentAdapter(connection);

  console.log("The external framework can map these neutral tools:");
  for (const tool of externalAgent.availableTools()) {
    console.log(`- ${tool.name}`);
  }
  console.log("Tool selection stays external; capability execution remains governed by Marona.");
}
