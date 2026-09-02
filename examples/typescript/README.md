# TypeScript examples

## Requirements

- Node.js 20+
- A required Marona Developer Key from <https://platform.marona.ai>

## Install and run

```bash
corepack enable
pnpm install --frozen-lockfile
export MARONA_API_KEY=mr_live_xxxxx # placeholder; use your real key locally
pnpm build
pnpm start basic-agent
```

Available examples are `basic-agent`, `multi-agent`, `bring-your-own-agent`,
`realtime-agent`, and `chat-server`.

## Verify

```bash
pnpm test
```
