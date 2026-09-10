package com.tyelaman.pulsewatch.check;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public CheckResult check(String url) {
        long startTime = System.nanoTime();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<Void> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            long responseTimeMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            String status =
                    response.statusCode() >= 200 && response.statusCode() < 400
                            ? "UP"
                            : "DOWN";

            return new CheckResult(
                    url,
                    status,
                    response.statusCode(),
                    responseTimeMs,
                    Instant.now(),
                    null
            );
        }
        catch (Exception exception) {
            long responseTimeMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            return new CheckResult(
                    url,
                    "DOWN",
                    null,
                    responseTimeMs,
                    Instant.now(),
                    exception.getClass().getSimpleName()
            );
        }
    }
}