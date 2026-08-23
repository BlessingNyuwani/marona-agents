import { runBasicAgent } from "./basic-agent.js";
import { runBringYourOwnAgent } from "./bring-your-own-agent.js";
import { runChatServer } from "./chat-server.js";
import { runMultiAgent } from "./multi-agent.js";
import { runRealtimeAgent } from "./realtime-agent.js";

const commands: Record<string, () => Promise<void>> = {
  "basic-agent": runBasicAgent,
  "multi-agent": runMultiAgent,
  "bring-your-own-agent": runBringYourOwnAgent,
  "realtime-agent": runRealtimeAgent,
  "chat-server": runChatServer,
};

const selected = process.argv[2];
const command = selected ? commands[selected] : undefined;
if (!command) {
  console.error(`Usage: npm start -- ${Object.keys(commands).join(" | ")}`);
  process.exitCode = 1;
} else {
  await command();
}
