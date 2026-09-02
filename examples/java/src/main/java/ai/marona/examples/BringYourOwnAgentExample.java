package ai.marona.examples;

import ai.marona.MCPConnection;
import ai.marona.mcp.MaronaMcp;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public final class BringYourOwnAgentExample {
    private BringYourOwnAgentExample() {
    }

    public interface CapabilityConnection {
        List<JsonNode> listTools();

        JsonNode callTool(
                String name,
                JsonNode input,
                String userId,
                String conversationId
        );
    }

    public static final class ExistingAgentAdapter {
        private final CapabilityConnection connection;

        public ExistingAgentAdapter(CapabilityConnection connection) {
            this.connection = connection;
        }

        public List<JsonNode> availableTools() {
            return connection.listTools();
        }

        public JsonNode executeSelectedTool(
                String name,
                JsonNode input,
                String userId,
                String sessionId
        ) {
            return connection.callTool(name, input, userId, sessionId);
        }
    }

    public static void run() {
        try (MaronaMcp mcp = new MaronaMcp(Configuration.requiredApiKey())) {
            MCPConnection connection = mcp.connect();
            ExistingAgentAdapter externalAgent = new ExistingAgentAdapter(
                    new MaronaCapabilityConnection(connection)
            );
            System.out.println("The external framework can map these neutral tools:");
            for (JsonNode tool : externalAgent.availableTools()) {
                System.out.println("- " + tool.path("name").asText("unnamed_tool"));
            }
            System.out.println(
                    "Tool selection stays external; capability execution remains governed by Marona."
            );
        }
    }

    private record MaronaCapabilityConnection(MCPConnection connection)
            implements CapabilityConnection {
        @Override
        public List<JsonNode> listTools() {
            return connection.listTools();
        }

        @Override
        public JsonNode callTool(
                String name,
                JsonNode input,
                String userId,
                String conversationId
        ) {
            return connection.callTool(name, input, userId, conversationId);
        }
    }
}
