# Expected output

The exact wording varies because `marona/auto` can route requests across
allowed models and providers. Successful text examples print a normalized Agent
response. Multi-agent examples also print the Agent that owns the final turn.

The realtime examples emit transcript deltas until a provider-neutral
`response.completed` event arrives. They require a model route that supports
the realtime transport.

The Chat UI servers expose:

- `GET /health` returning `{"status":"ok"}`
- `POST /api/chat` accepting `message`, `userId`, and `sessionId`
- `GET /` serving the shared browser client

Tests build Agent graphs and exercise local adapters without making paid model
requests.
