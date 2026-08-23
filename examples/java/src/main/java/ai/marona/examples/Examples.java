package ai.marona.examples;

import java.util.Map;

public final class Examples {
    private Examples() {
    }

    public static void main(String[] args) {
        Map<String, Runnable> examples = Map.of(
                "basic-agent", BasicAgentExample::run,
                "multi-agent", MultiAgentExample::run,
                "bring-your-own-agent", BringYourOwnAgentExample::run,
                "realtime-agent", RealtimeAgentExample::run,
                "chat-server", ChatServer::run
        );

        String selected = args.length == 0 ? "" : args[0];
        Runnable example = examples.get(selected);
        if (example == null) {
            System.err.println(
                    "Usage: basic-agent | multi-agent | bring-your-own-agent "
                            + "| realtime-agent | chat-server"
            );
            System.exit(2);
        }
        example.run();
    }
}
