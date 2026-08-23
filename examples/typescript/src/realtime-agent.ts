import { Agent, Marona, RealtimeRunner } from "marona";

import { requiredApiKey, selectedModel } from "./config.js";

export function buildRealtimeAgent(): { marona: Marona; agent: Agent } {
  const marona = new Marona({ apiKey: requiredApiKey() });
  const agent = new Agent({
    name: "Realtime Assistant",
    model: selectedModel(),
    instructions: "Respond naturally and keep spoken answers concise.",
    voice: {
      output_voice: "ash",
      language: "en",
      turn_detection: "semantic_vad",
      interruptible: true,
      input_format: "pcm16",
      output_format: "pcm16",
    },
    modalities: ["text", "audio"],
    outputModalities: ["text", "audio"],
  });
  return { marona, agent };
}

export async function runRealtimeAgent(): Promise<void> {
  const { agent } = buildRealtimeAgent();
  const session = await RealtimeRunner.connect(agent, {
    userId: "example-user",
    sessionId: "realtime-agent-session",
  });
  try {
    await session.sendMessage("Introduce yourself briefly.");
    for await (const event of session) {
      if (event.type === "transcript.delta" && event.delta) {
        process.stdout.write(event.delta);
      } else if (event.type === "response.completed") {
        process.stdout.write("\n");
        break;
      }
    }
  } finally {
    await session.close();
  }
}
