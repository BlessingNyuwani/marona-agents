package ai.marona.examples;

import ai.marona.Agent;
import ai.marona.Marona;
import ai.marona.ToolContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExamplesTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Test
    void basicAgentUsesProviderNeutralDefaults() {
        try (Marona marona = new Marona("test-key")) {
            Agent agent = BasicAgentExample.buildAgent(marona);
            assertEquals("Customer Assistant", agent.name());
            assertEquals("marona/auto", agent.model());
        }
    }

    @Test
    void multiAgentGraphBuildsWithoutNetworkAccess() {
        try (Marona marona = new Marona("test-key")) {
            Agent manager = MultiAgentExample.buildAgentGraph(marona);
            assertEquals("Customer Support Manager", manager.name());
        }
    }

    @Test
    void localFunctionToolUsesTrustedIdentityContext() {
        ObjectNode input = JSON.createObjectNode()
                .put("amount", 24)
                .put("duplicate_charge", true);
        ToolContext context = new ToolContext(
                "developer-1",
                "session-1",
                null,
                List.of("billing.refund.read"),
                List.of(),
                null
        );

        JsonNode result = MultiAgentExample.checkRefundTool().invoke(input, context);

        assertTrue(result.path("eligible").asBoolean());
        assertEquals(24, result.path("refund_amount").asInt());
        assertEquals("developer-1", result.path("customer_id").asText());
    }

    @Test
    void existingAgentAdapterPreservesIdentityContext() {
        List<String> calls = new ArrayList<>();
        BringYourOwnAgentExample.CapabilityConnection connection =
                new BringYourOwnAgentExample.CapabilityConnection() {
                    @Override
                    public List<JsonNode> listTools() {
                        return List.of(JSON.createObjectNode().put("name", "example__lookup"));
                    }

                    @Override
                    public JsonNode callTool(
                            String name,
                            JsonNode input,
                            String userId,
                            String conversationId
                    ) {
                        calls.add(name + ":" + userId + ":" + conversationId);
                        return JSON.createObjectNode().put("ok", true);
                    }
                };
        BringYourOwnAgentExample.ExistingAgentAdapter adapter =
                new BringYourOwnAgentExample.ExistingAgentAdapter(connection);

        JsonNode result = adapter.executeSelectedTool(
                "example__lookup",
                JSON.createObjectNode().put("query", "Marona"),
                "developer-1",
                "session-1"
        );

        assertTrue(result.path("ok").asBoolean());
        assertEquals(List.of("example__lookup:developer-1:session-1"), calls);
    }
}
