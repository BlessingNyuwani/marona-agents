package ai.marona.examples;

import ai.marona.Agent;
import ai.marona.Marona;
import ai.marona.RealtimeEvent;
import ai.marona.RealtimeRunner;
import ai.marona.RealtimeSession;
import ai.marona.VoiceOptions;

public final class RealtimeAgentExample {
    private RealtimeAgentExample() {
    }

    public static Agent buildAgent(Marona marona) {
        VoiceOptions voice = VoiceOptions.builder()
                .outputVoice("ash")
                .language("en")
                .turnDetection("semantic_vad")
                .interruptible(true)
                .inputFormat("pcm16")
                .outputFormat("pcm16")
                .build();

        return new Agent(marona, "Realtime Assistant")
                .model(Configuration.selectedModel())
                .instructions("Respond naturally and keep spoken answers concise.")
                .voice(voice)
                .modalities("text", "audio")
                .outputModalities("text", "audio");
    }

    public static void run() {
        try (Marona marona = new Marona(Configuration.requiredApiKey())) {
            Agent agent = buildAgent(marona);
            try (RealtimeSession session = RealtimeRunner.connect(
                    agent,
                    "example-user",
                    "realtime-agent-session"
            )) {
                session.sendMessage("Introduce yourself briefly.");
                while (true) {
                    RealtimeEvent event = session.nextEvent();
                    if ("transcript.delta".equals(event.type()) && event.delta() != null) {
                        System.out.print(event.delta());
                    } else if ("response.completed".equals(event.type())) {
                        System.out.println();
                        break;
                    }
                }
            }
        }
    }
}
