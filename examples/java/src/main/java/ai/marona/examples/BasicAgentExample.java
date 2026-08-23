package ai.marona.examples;

import ai.marona.Agent;
import ai.marona.Input;
import ai.marona.Marona;
import ai.marona.RunResult;
import ai.marona.Runner;

public final class BasicAgentExample {
    private BasicAgentExample() {
    }

    public static Agent buildAgent(Marona marona) {
        return new Agent(marona, "Customer Assistant")
                .model(Configuration.selectedModel())
                .instructions(
                        "Answer clearly and concisely. "
                                + "Use connected capabilities only when they are needed."
                );
    }

    public static void run() {
        try (Marona marona = new Marona(Configuration.requiredApiKey())) {
            Agent agent = buildAgent(marona);
            RunResult result = Runner.run(
                    agent,
                    Input.text("Explain what the Marona AI Runtime does in three sentences."),
                    "example-user",
                    "basic-agent-session"
            );
            System.out.println(result.output().toPrettyString());
        }
    }
}
