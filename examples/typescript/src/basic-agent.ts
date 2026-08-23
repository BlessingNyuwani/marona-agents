import { Agent, Marona, Runner } from "marona";

import { requiredApiKey, selectedApp, selectedModel } from "./config.js";

export async function buildAgent(): Promise<{ marona: Marona; agent: Agent }> {
  const marona = new Marona({ apiKey: requiredApiKey() });
  const app = selectedApp();
  const tools = app ? await marona.hub.connect({ apps: [app] }) : undefined;
  const agent = new Agent({
    name: "Customer Assistant",
    model: selectedModel(),
    instructions: "Answer clearly and concisely. Use connected capabilities only when needed.",
    ...(tools ? { tools } : {}),
    toolChoice: "auto",
  });
  return { marona, agent };
}

export async function runBasicAgent(): Promise<void> {
  const { agent } = await buildAgent();
  const result = await Runner.run(
    agent,
    "Explain what the Marona AI Runtime does in three sentences.",
    { userId: "example-user", sessionId: "basic-agent-session" },
  );
  console.log(result.output);
}
