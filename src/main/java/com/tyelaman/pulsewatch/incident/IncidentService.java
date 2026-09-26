package com.tyelaman.pulsewatch.incident;

import java.time.Duration;
import java.time.Instant;

import com.tyelaman.pulsewatch.ai.IncidentSummaryService;
import com.tyelaman.pulsewatch.check.CheckResult;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {

    private static final int FAILURE_THRESHOLD = 3;

    private final IncidentSummaryService incidentSummaryService;

    private int consecutiveFailures = 0;
    private Instant firstFailureAt = null;
    private Incident latestIncident = null;

    public IncidentService(
            IncidentSummaryService incidentSummaryService) {

        this.incidentSummaryService = incidentSummaryService;
    }

    public void processCheck(
            String serviceName,
            CheckResult result) {

        if ("DOWN".equals(result.status())) {
            if (consecutiveFailures == 0) {
                firstFailureAt = result.checkedAt();
            }
            consecutiveFailures++;

            System.out.println(
                    "Consecutive failures: " + consecutiveFailures
            );

            if (latestIncident != null && latestIncident.isOpen()) {
                latestIncident.recordFailure(result.statusCode());
                return;
            }

            if (consecutiveFailures >= FAILURE_THRESHOLD) {
                latestIncident = new Incident(
                        serviceName,
                        result.url(),
                        firstFailureAt,
                        result.statusCode(),
                        consecutiveFailures
                );

                String summary =
                        incidentSummaryService.generateSummary(latestIncident);

                latestIncident.setAiSummary(summary);

                System.out.println("!!! INCIDENT OPENED !!!");
                System.out.println(
                        "Service: " + latestIncident.getServiceName()
                );
                System.out.println(
                        "Started: " + latestIncident.getStartedAt()
                );
                System.out.println(
                        "HTTP status: " + latestIncident.getLastStatusCode()
                );
                System.out.println(
                        "AI Summary: " + latestIncident.getAiSummary()
                );
            }

            return;
        }

        consecutiveFailures = 0;
        firstFailureAt = null;

        if (latestIncident != null && latestIncident.isOpen()) {
            latestIncident.resolve(result.checkedAt());

            long durationSeconds = Duration.between(
                    latestIncident.getStartedAt(),
                    latestIncident.getResolvedAt()
            ).toSeconds();

            String summary =
                    incidentSummaryService.generateSummary(latestIncident);

            latestIncident.setAiSummary(summary);

            System.out.println("!!! INCIDENT RESOLVED !!!");
            System.out.println(
                    "Service: " + latestIncident.getServiceName()
            );
            System.out.println(
                    "Duration: " + durationSeconds + " seconds"
            );
            System.out.println(
                    "AI Summary: " + latestIncident.getAiSummary()
            );
        }
    }

    public Incident getLatestIncident() {
        return latestIncident;
    }
}