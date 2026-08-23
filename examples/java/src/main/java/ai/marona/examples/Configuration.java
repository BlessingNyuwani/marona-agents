package ai.marona.examples;

final class Configuration {
    private Configuration() {
    }

    static String requiredApiKey() {
        String value = System.getenv("MARONA_API_KEY");
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "MARONA_API_KEY is required. Create one at https://platform.marona.ai."
            );
        }
        return value.trim();
    }

    static String selectedModel() {
        String value = System.getenv("MARONA_MODEL");
        return value == null || value.isBlank() ? "marona/auto" : value.trim();
    }
}
