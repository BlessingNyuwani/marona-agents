# Security policy

## Reporting a vulnerability

Do not open a public issue for a suspected vulnerability. Email
`security@marona.ai` with the affected example, reproduction steps, impact, and
any suggested remediation. You should receive an acknowledgement within three
business days.

## Example security boundaries

- Keep `MARONA_API_KEY` and provider credentials in environment variables.
- Never place credentials in agent instructions, model input, logs, images, or
  container layers.
- Treat connected MCP Apps, A2A peers, uploaded content, and model output as
  untrusted input.
- Use the minimum permissions required for local tools and connected Apps.
- Do not expose these demonstration servers directly to the internet without
  authentication, rate limiting, TLS, and an application-specific review.

Only the latest commit on the default branch is supported.
