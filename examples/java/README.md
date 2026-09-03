# Java examples

## Requirements

- Java 17+
- Maven 3.9+
- A required Marona Developer Key from <https://platform.marona.ai>

Maven resolves `ai.marona:marona:1.0.0` directly from Maven Central. No sibling
checkout or local SDK installation is required.

## Run

```bash
export MARONA_API_KEY=mr_live_xxxxx # placeholder; use your real key locally
mvn compile exec:java -Dexec.mainClass=ai.marona.examples.Examples \
  -Dexec.args=basic-agent
```

Available examples are `basic-agent`, `multi-agent`, `bring-your-own-agent`,
`realtime-agent`, and `chat-server`.

## Verify

```bash
mvn test
```
