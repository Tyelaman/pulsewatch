package com.tyelaman.pulsewatch.ai;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tyelaman.pulsewatch.incident.Incident;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IncidentSummaryService {

    private static final String UNAVAILABLE =
            "AI summary unavailable.";

    private final String provider;
    private final String ollamaModel;
    private final String groqModel;
    private final String groqApiKey;

    private final RestClient ollamaClient;
    private final RestClient groqClient;

    public IncidentSummaryService(
            @Value("${pulsewatch.ai.provider}") String provider,
            @Value("${pulsewatch.ai.ollama.url}") String ollamaUrl,
            @Value("${pulsewatch.ai.ollama.model}") String ollamaModel,
            @Value("${pulsewatch.ai.groq.url}") String groqUrl,
            @Value("${pulsewatch.ai.groq.model}") String groqModel,
            @Value("${pulsewatch.ai.groq.api-key}") String groqApiKey) {

        this.provider = provider;
        this.ollamaModel = ollamaModel;
        this.groqModel = groqModel;
        this.groqApiKey = groqApiKey;

        this.ollamaClient = RestClient.builder()
                .baseUrl(ollamaUrl)
                .build();

        this.groqClient = RestClient.builder()
                .baseUrl(groqUrl)
                .build();

        System.out.println("AI provider: " + provider);
    }

    public String generateSummary(Incident incident) {

        String prompt;

        if (incident.isOpen()) {
            prompt = """
            You are an incident assistant for an API monitoring system.

            Summarize this ongoing incident in exactly two sentences.

            Rules:
            - State that the incident is ongoing.
            - Mention the observed HTTP status.
            - Do not mention duration, timestamps, or failure counts.
            - Never invent a root cause or speculate.
            - Use only the supplied information.
            - Do not use markdown.

            Service: %s
            Latest failed HTTP status: %s
            """.formatted(
                    incident.getServiceName(),
                    incident.getLastStatusCode()
            );
        }
        else {
            long durationSeconds = Duration.between(
                    incident.getStartedAt(),
                    incident.getResolvedAt()
            ).toSeconds();

            prompt = """
            You are an incident assistant for an API monitoring system.

            Summarize this resolved incident in exactly two sentences.

            Rules:
            - Mention that the service recovered.
            - Include the exact duration in seconds.
            - Include the final recorded failure count.
            - Mention the observed HTTP status.
            - Never invent a root cause or speculate.
            - Do not include timestamps.
            - Do not use markdown.

            Service: %s
            Incident status: RESOLVED
            Duration: %d seconds
            Final recorded failures: %d
            Last failed HTTP status: %s
            """.formatted(
                    incident.getServiceName(),
                    durationSeconds,
                    incident.getFailureCount(),
                    incident.getLastStatusCode()
            );
        }

        try {
            if ("groq".equalsIgnoreCase(provider)) {
                return generateGroqSummary(prompt);
            }

            if ("ollama".equalsIgnoreCase(provider)) {
                return generateOllamaSummary(prompt);
            }

            System.out.println("Unknown AI provider: " + provider);

            return UNAVAILABLE;
        }
        catch (Exception exception) {
            System.out.println(
                    "AI summary generation failed: " +
                            exception.getClass().getSimpleName()
            );

            return UNAVAILABLE;
        }
    }

    private String generateOllamaSummary(String prompt) {

        OllamaResponse response = ollamaClient.post()
                .uri("/api/chat")
                .body(new OllamaRequest(
                        ollamaModel,
                        List.of(new ChatMessage("user", prompt)),
                        false
                ))
                .retrieve()
                .body(OllamaResponse.class);

        if (response == null || response.message() == null) {
            return UNAVAILABLE;
        }

        return validSummary(response.message().content());
    }

    private String generateGroqSummary(String prompt) {

        if (groqApiKey.isBlank()) {
            System.out.println("Groq API key is missing.");
            return UNAVAILABLE;
        }

        GroqResponse response = groqClient.post()
                .uri("/chat/completions")
                .headers(headers ->
                        headers.setBearerAuth(groqApiKey)
                )
                .body(new GroqRequest(
                        groqModel,
                        List.of(new ChatMessage("user", prompt))
                ))
                .retrieve()
                .body(GroqResponse.class);

        if (response == null ||
                response.choices() == null ||
                response.choices().isEmpty() ||
                response.choices().get(0).message() == null) {

            return UNAVAILABLE;
        }

        return validSummary(
                response.choices().get(0).message().content()
        );
    }

    private String validSummary(String summary) {
        if (summary == null || summary.isBlank()) {
            return UNAVAILABLE;
        }

        return summary;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ChatMessage(
            String role,
            String content
    ) {
    }

    private record OllamaRequest(
            String model,
            List<ChatMessage> messages,
            boolean stream
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaResponse(
            ChatMessage message
    ) {
    }

    private record GroqRequest(
            String model,
            List<ChatMessage> messages
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GroqChoice(
            ChatMessage message
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GroqResponse(
            List<GroqChoice> choices
    ) {
    }
}