# Marona Agents

Production-shaped examples for building provider-neutral AI Agents with
[Marona AI Runtime](https://platform.marona.ai). The repository demonstrates a
managed Agent, a multi-agent graph, bring-your-own-agent integration, realtime
voice, and a shared Chat UI in Python, TypeScript, and Java.

Created and maintained by **Blessing Nyuwani**, Applied AI Engineer and creator
of Marona AI Runtime.

## Examples

| Capability | Python | TypeScript | Java |
| --- | :---: | :---: | :---: |
| Marona Agent | ✓ | ✓ | ✓ |
| Multi-agent delegation and handoff | ✓ | ✓ | ✓ |
| Bring your own Agent | ✓ | ✓ | ✓ |
| Realtime voice | ✓ | ✓ | ✓ |
| Chat UI server | ✓ | ✓ | ✓ |
| Automated contract tests | ✓ | ✓ | ✓ |

Each language is independent:

- [Python](examples/python/README.md) uses `marona==0.13.9`.
- [TypeScript](examples/typescript/README.md) uses `marona@0.13.8` with a frozen lockfile.
- [Java](examples/java/README.md) uses `ai.marona:marona:0.14.2`,
  `ai.marona:marona-sdk:0.14.2`, and Java 17.

## Quick start

Create an API key at <https://platform.marona.ai>, then run the Python Chat UI:

```bash
cp .env.example .env
# Set MARONA_API_KEY in .env.
docker compose up --build
```

Open <http://127.0.0.1:8080>. To run the TypeScript backend instead:

```bash
docker compose --profile typescript up --build typescript-chat
```

The browser client is dependency-free and contains no API key. See the
[architecture](docs/architecture.md) and [expected output](docs/expected-output.md).

## Repository structure

```text
marona-agents/
├── apps/chat-ui/            # Shared accessible browser client
├── examples/python/         # Python package, tests, and container
├── examples/typescript/     # TypeScript package, tests, lockfile, and container
├── examples/java/           # Java 17 Maven project and tests
├── docs/                    # Architecture and expected behavior
└── compose.yaml             # Reproducible local Chat UI runtimes
```

## Configuration

| Variable | Required | Default | Purpose |
| --- | --- | --- | --- |
| `MARONA_API_KEY` | Yes | — | Authenticates the server-side runtime client |
| `MARONA_MODEL` | No | `marona/auto` | Selects a provider-neutral model route |
| `MARONA_APP_SLUG` | No | — | Connects one public or developer-owned MCP App |
| `PORT` | No | `8080` | Changes the Chat UI server port |
| `CHAT_UI_DIR` | No | Language default | Overrides the shared static client path |

Never commit an API key. The examples pass only stable developer-owned user and
session identifiers to Marona; they do not expose provider credentials.

## Java package availability

The Java examples resolve the published Marona packages directly from Maven
Central. The Agent examples use `ai.marona:marona`; the bring-your-own-agent
example uses the focused `ai.marona:marona-sdk` MCP entry point.

## Contributing and security

Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a change. Report security
issues privately using [SECURITY.md](SECURITY.md), not a public issue.

## License

MIT © 2026 Blessing Nyuwani. See [LICENSE](LICENSE).
