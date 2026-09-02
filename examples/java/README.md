# Java examples

## Requirements

- Java 17+
- Maven 3.9+
- A Marona API key from <https://platform.marona.ai>

Maven resolves `ai.marona:marona:0.14.2` and
`ai.marona:marona-sdk:0.14.2` directly from Maven Central. No sibling checkout
or local SDK installation is required.

## Run

```bash
export MARONA_API_KEY=replace_with_your_marona_api_key
mvn compile exec:java -Dexec.mainClass=ai.marona.examples.Examples \
  -Dexec.args=basic-agent
```

Available examples are `basic-agent`, `multi-agent`, `bring-your-own-agent`,
`realtime-agent`, and `chat-server`.

## Verify

```bash
mvn test
```
