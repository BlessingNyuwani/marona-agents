package ai.marona.examples;

import ai.marona.Agent;
import ai.marona.FunctionTool;
import ai.marona.Handoff;
import ai.marona.Input;
import ai.marona.Marona;
import ai.marona.RunResult;
import ai.marona.Runner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Duration;

public final class MultiAgentExample {
    private static final ObjectMapper JSON = new ObjectMapper();

    private MultiAgentExample() {
    }

    public static FunctionTool checkRefundTool() {
        ObjectNode schema = JSON.createObjectNode().put("type", "object");
        ObjectNode properties = schema.putObject("properties");
        properties.putObject("amount").put("type", "number");
        properties.putObject("duplicate_charge").put("type", "boolean");
        schema.putArray("required").add("amount").add("duplicate_charge");
        schema.put("additionalProperties", false);

        return FunctionTool.sync(
                        "check_refund",
                        "Check whether a duplicate charge is eligible for a refund.",
                        schema,
                        MultiAgentExample::checkRefund
                )
                .permissions("billing.refund.read")
                .timeout(Duration.ofSeconds(5))
                .build();
    }

    private static JsonNode checkRefund(JsonNode input, ai.marona.ToolContext context) {
        double amount = input.path("amount").asDouble();
        boolean duplicateCharge = input.path("duplicate_charge").asBoolean();
        ObjectNode result = JSON.createObjectNode()
                .put("eligible", duplicateCharge && amount > 0)
                .put("refund_amount", duplicateCharge ? amount : 0);
        if (context.userId() != null) {
            result.put("customer_id", context.userId());
        }
        return result;
    }

    public static Agent buildAgentGraph(Marona marona) {
        Agent researcher = new Agent(marona, "Research Specialist")
                .description("Researches one bounded customer question.")
                .model(Configuration.selectedModel())
                .instructions("Return a concise factual summary to the manager.");

        ObjectNode billingMetadata = JSON.createObjectNode();
        billingMetadata.putArray("permissions").add("billing.refund.read");
        Agent billing = new Agent(marona, "Billing Specialist")
                .description("Owns billing and payment conversations.")
                .model(Configuration.selectedModel())
                .instructions("Use validated billing tools before making a decision.")
                .tool(checkRefundTool())
                .metadata(billingMetadata);

        return new Agent(marona, "Customer Support Manager")
                .model(Configuration.selectedModel())
                .instructions("Delegate research and hand off billing requests.")
                .agentTool(researcher.asTool(
                        "research",
                        "Research one bounded question and return the result."
                ))
                .handoff(new Handoff(
                        billing,
                        "transfer_to_billing",
                        "Transfer billing and payment requests."
                ));
    }

    public static void run() {
        try (Marona marona = new Marona(Configuration.requiredApiKey())) {
            Agent manager = buildAgentGraph(marona);
            RunResult result = Runner.run(
                    manager,
                    Input.text(
                            "I was charged twice for USD 24. "
                                    + "Check whether I qualify for a refund."
                    ),
                    "example-user",
                    "multi-agent-session"
            );
            System.out.println(result.output().toPrettyString());
            System.out.println("Active agent: " + result.activeAgent());
        }
    }
}
