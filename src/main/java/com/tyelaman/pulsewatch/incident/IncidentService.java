package com.tyelaman.pulsewatch.incident;

import java.time.Duration;

import com.tyelaman.pulsewatch.check.CheckResult;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {

    private static final int FAILURE_THRESHOLD = 3;

    private int consecutiveFailures = 0;
    private Incident latestIncident = null;

    public void processCheck(
            String serviceName,
            CheckResult result) {

        if ("DOWN".equals(result.status())) {
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
                        result.checkedAt(),
                        result.statusCode(),
                        consecutiveFailures
                );

                System.out.println(
                        "!!! INCIDENT OPENED !!!"
                );
                System.out.println(
                        "Service: " + latestIncident.getServiceName()
                );
                System.out.println(
                        "Started: " + latestIncident.getStartedAt()
                );
                System.out.println(
                        "HTTP status: " + latestIncident.getLastStatusCode()
                );
            }

            return;
        }

        consecutiveFailures = 0;

        if (latestIncident != null && latestIncident.isOpen()) {
            latestIncident.resolve(result.checkedAt());

            long durationSeconds = Duration.between(
                    latestIncident.getStartedAt(),
                    latestIncident.getResolvedAt()
            ).toSeconds();

            System.out.println(
                    "!!! INCIDENT RESOLVED !!!"
            );
            System.out.println(
                    "Service: " + latestIncident.getServiceName()
            );
            System.out.println(
                    "Duration: " + durationSeconds + " seconds"
            );
        }
    }

    public Incident getLatestIncident() {
        return latestIncident;
    }
}