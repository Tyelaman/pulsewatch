package com.tyelaman.pulsewatch.ai;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tyelaman.pulsewatch.incident.Incident;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IncidentSummaryService {

    private static final String OLLAMA_URL =
            "http://localhost:11434";

    private static final String MODEL =
            "llama3.2:3b";

    private final RestClient restClient;

    public IncidentSummaryService() {
        restClient = RestClient.builder()
                .baseUrl(OLLAMA_URL)
                .build();
    }

    public String generateSummary(Incident incident) {
        String duration = "Ongoing";

        if (incident.getResolvedAt() != null) {
            long durationSeconds = Duration.between(
                    incident.getStartedAt(),
                    incident.getResolvedAt()
            ).toSeconds();

            duration = durationSeconds + " seconds";
        }

        String prompt = """
        You are an incident assistant for an API monitoring system.

        Summarize the incident in exactly 2 concise sentences.
        Use only the facts provided.
        Do not guess or invent a root cause.
        Do not use markdown.

        Service: %s
        URL: %s
        Incident status: %s
        Started at: %s
        Resolved at: %s
        Duration: %s
        Last HTTP status: %s
        Recorded failures: %d
        """.formatted(
                incident.getServiceName(),
                incident.getUrl(),
                incident.getStatus(),
                incident.getStartedAt(),
                incident.getResolvedAt() == null
                        ? "Not resolved yet"
                        : incident.getResolvedAt(),
                duration,
                incident.getLastStatusCode(),
                incident.getFailureCount()
        );

        try {
            OllamaResponse response = restClient.post()
                    .uri("/api/chat")
                    .body(new OllamaRequest(
                            MODEL,
                            List.of(
                                    new OllamaMessage("user", prompt)
                            ),
                            false
                    ))
                    .retrieve()
                    .body(OllamaResponse.class);

            if (response == null || response.message() == null) {
                return "AI summary unavailable.";
            }

            return response.message().content();
        }
        catch (Exception exception) {
            System.out.println(
                    "AI summary generation failed: " +
                            exception.getMessage()
            );

            return "AI summary unavailable.";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaMessage(
            String role,
            String content
    ) {
    }

    private record OllamaRequest(
            String model,
            List<OllamaMessage> messages,
            boolean stream
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaResponse(
            OllamaMessage message
    ) {
    }
}