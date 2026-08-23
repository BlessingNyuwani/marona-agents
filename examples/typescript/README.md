# TypeScript examples

## Requirements

- Node.js 20+
- A Marona API key from <https://platform.marona.ai>

## Install and run

```bash
corepack enable
pnpm install --frozen-lockfile
export MARONA_API_KEY=replace_with_your_marona_api_key
pnpm build
pnpm start basic-agent
```

Available examples are `basic-agent`, `multi-agent`, `bring-your-own-agent`,
`realtime-agent`, and `chat-server`.

## Verify

```bash
pnpm test
```
