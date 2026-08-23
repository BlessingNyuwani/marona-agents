package ai.marona.examples;

import ai.marona.Agent;
import ai.marona.Input;
import ai.marona.Marona;
import ai.marona.RunResult;
import ai.marona.Runner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Executors;

public final class ChatServer {
    private static final int MAX_REQUEST_BYTES = 32_768;
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".css", "text/css; charset=utf-8",
            ".html", "text/html; charset=utf-8",
            ".js", "text/javascript; charset=utf-8",
            ".svg", "image/svg+xml"
    );

    private ChatServer() {
    }

    public static void run() {
        try {
            Marona marona = new Marona(Configuration.requiredApiKey());
            Agent agent = BasicAgentExample.buildAgent(marona);
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
            Path uiDirectory = Path.of(
                    System.getenv().getOrDefault("CHAT_UI_DIR", "../../apps/chat-ui")
            ).toAbsolutePath().normalize();
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            server.createContext("/health", exchange -> json(
                    exchange,
                    200,
                    JSON.createObjectNode().put("status", "ok")
            ));
            server.createContext("/api/chat", exchange -> chat(exchange, agent));
            server.createContext("/", exchange -> staticFile(exchange, uiDirectory));
            server.setExecutor(Executors.newCachedThreadPool());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop(1);
                marona.close();
            }));
            server.start();
            System.out.println("Marona Chat UI available at http://127.0.0.1:" + port);
        } catch (IOException error) {
            throw new IllegalStateException("Could not start the Chat UI server.", error);
        }
    }

    private static void chat(HttpExchange exchange, Agent agent) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            json(exchange, 404, JSON.createObjectNode().put("error", "not_found"));
            return;
        }
        try {
            byte[] body = exchange.getRequestBody().readNBytes(MAX_REQUEST_BYTES + 1);
            if (body.length > MAX_REQUEST_BYTES) {
                throw new IllegalArgumentException("Request body exceeds 32 KiB.");
            }
            JsonNode payload = JSON.readTree(body);
            String message = payload.path("message").asText("").trim();
            if (message.isEmpty()) {
                throw new IllegalArgumentException("message is required");
            }
            String userId = payload.path("userId").asText("example-user");
            String sessionId = payload.path("sessionId").asText("chat-ui-session");
            RunResult result = Runner.run(agent, Input.text(message), userId, sessionId);
            ObjectNode response = JSON.createObjectNode().set("output", result.output());
            json(exchange, 200, response);
        } catch (IllegalArgumentException error) {
            json(exchange, 400, JSON.createObjectNode().put("error", error.getMessage()));
        } catch (RuntimeException error) {
            json(exchange, 500, JSON.createObjectNode().put("error", "request_failed"));
        }
    }

    private static void staticFile(HttpExchange exchange, Path uiDirectory) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            json(exchange, 404, JSON.createObjectNode().put("error", "not_found"));
            return;
        }
        String requested = "/".equals(exchange.getRequestURI().getPath())
                ? "index.html"
                : exchange.getRequestURI().getPath().substring(1);
        Path candidate = uiDirectory.resolve(requested).normalize();
        if (!candidate.startsWith(uiDirectory) || !Files.isRegularFile(candidate)) {
            json(exchange, 404, JSON.createObjectNode().put("error", "not_found"));
            return;
        }
        byte[] body = Files.readAllBytes(candidate);
        String filename = candidate.getFileName().toString();
        String extension = filename.contains(".")
                ? filename.substring(filename.lastIndexOf('.'))
                : "";
        exchange.getResponseHeaders().set(
                "Content-Type",
                CONTENT_TYPES.getOrDefault(extension, "application/octet-stream")
        );
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static void json(HttpExchange exchange, int status, JsonNode payload)
            throws IOException {
        byte[] body = JSON.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }
}
