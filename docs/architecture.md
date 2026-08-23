# Architecture

The repository keeps each language independent while sharing one intentionally
small browser client.

```text
apps/chat-ui
    │ POST /api/chat
    ├── Python adapter ─────┐
    ├── TypeScript adapter ├── Marona Agent ── Marona AI Runtime
    └── Java adapter ──────┘          │
                                      ├── MCP Apps
                                      ├── Skills
                                      └── delegated and handed-off Agents
```

API keys are read only by the server process. The browser receives normalized
Agent output and never receives runtime credentials. Every example supplies a
stable user ID and session ID so conversation state stays isolated.

The bring-your-own-agent examples deliberately define a narrow adapter. An
existing framework may choose a tool, but Marona remains responsible for
capability discovery, identity context, policy enforcement, and execution.
