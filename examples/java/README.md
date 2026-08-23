# Java examples

## Requirements

- Java 17+
- Maven 3.9+
- A Marona API key from <https://platform.marona.ai>

The Java SDK is currently installed from the adjacent Marona AI Runtime source
until `ai.marona:marona` is published to Maven Central:

```bash
cd ../../../edge-node-service/clients/java
mvn install
cd ../../../marona-agents/examples/java
```

After the artifact is public, only the normal `mvn` commands below will be
required.

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
