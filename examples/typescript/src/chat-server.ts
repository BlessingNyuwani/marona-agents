import { createReadStream, existsSync } from "node:fs";
import { createServer, type ServerResponse } from "node:http";
import { extname, resolve, sep } from "node:path";

import { Runner } from "marona";

import { buildAgent } from "./basic-agent.js";

const contentTypes: Record<string, string> = {
  ".css": "text/css; charset=utf-8",
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".svg": "image/svg+xml",
};

function json(response: ServerResponse, status: number, body: unknown): void {
  response.writeHead(status, { "content-type": "application/json; charset=utf-8" });
  response.end(JSON.stringify(body));
}

export async function runChatServer(): Promise<void> {
  const { agent } = await buildAgent();
  const port = Number.parseInt(process.env.PORT || "8080", 10);
  const uiDirectory = resolve(process.env.CHAT_UI_DIR || "apps/chat-ui");
  const server = createServer(async (request, response) => {
    if (request.method === "GET" && request.url === "/health") {
      json(response, 200, { status: "ok" });
      return;
    }
    if (request.method === "POST" && request.url === "/api/chat") {
      try {
        const chunks: Buffer[] = [];
        let size = 0;
        for await (const chunk of request) {
          const buffer = Buffer.from(chunk);
          size += buffer.length;
          if (size > 32_768) throw new Error("Request body exceeds 32 KiB.");
          chunks.push(buffer);
        }
        const payload = JSON.parse(Buffer.concat(chunks).toString("utf8")) as {
          message?: unknown;
          userId?: unknown;
          sessionId?: unknown;
        };
        const message = String(payload.message || "").trim();
        if (!message) throw new Error("message is required");
        const result = await Runner.run(agent, message, {
          userId: String(payload.userId || "example-user"),
          sessionId: String(payload.sessionId || "chat-ui-session"),
        });
        json(response, 200, { output: result.output });
      } catch (error) {
        const message = error instanceof Error ? error.message : "Invalid request.";
        json(response, 400, { error: message });
      }
      return;
    }
    if (request.method !== "GET") {
      json(response, 404, { error: "not_found" });
      return;
    }
    const requested = request.url === "/" ? "index.html" : decodeURIComponent(request.url || "").slice(1);
    const candidate = resolve(uiDirectory, requested);
    if (!candidate.startsWith(`${uiDirectory}${sep}`) || !existsSync(candidate)) {
      json(response, 404, { error: "not_found" });
      return;
    }
    response.writeHead(200, {
      "content-type": contentTypes[extname(candidate)] || "application/octet-stream",
    });
    createReadStream(candidate).pipe(response);
  });
  server.listen(port, "0.0.0.0", () => {
    console.log(`Marona Chat UI available at http://127.0.0.1:${port}`);
  });
}
