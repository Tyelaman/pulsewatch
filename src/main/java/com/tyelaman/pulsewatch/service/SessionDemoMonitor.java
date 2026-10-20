package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.ai.IncidentSummaryService;
import com.tyelaman.pulsewatch.model.DemoSessionState;
import com.tyelaman.pulsewatch.model.Incident;
import com.tyelaman.pulsewatch.enums.CheckStatus;
import com.tyelaman.pulsewatch.model.CheckResult;

import java.time.Instant;

public class SessionDemoMonitor {

    private static final int CHECK_INTERVAL_SECONDS = 5;
    private static final String DEMO_SERVICE_NAME = "Demo Service";
    private static final String DEMO_URL = "demo://session";

    private Instant lastCheckedAt = null;
    private CheckResult latestResult = null;
    private final DemoSessionState state;
    private final IncidentService incidentService;

    public SessionDemoMonitor(
            IncidentSummaryService summaryService) {

        this.state = new DemoSessionState();
        this.incidentService =
                new IncidentServiceImpl(summaryService);
    }

    public synchronized CheckResult checkIfDue() {
        Instant now = Instant.now();

        if (lastCheckedAt != null &&
                now.isBefore(
                        lastCheckedAt.plusSeconds(CHECK_INTERVAL_SECONDS))) {
            return latestResult;
        }

        CheckStatus status = state.isHealthy()
                ? CheckStatus.UP
                : CheckStatus.DOWN;

        int statusCode = status == CheckStatus.UP ? 200 : 500;

        CheckResult result = new CheckResult(
                DEMO_URL,
                status,
                statusCode,
                0L,
                now,
                null
        );

        lastCheckedAt = now;
        latestResult = result;

        incidentService.processCheck(DEMO_SERVICE_NAME, result);

        return result;
    }

    public synchronized CheckResult getLatestResult() {
        return latestResult;
    }

    public DemoSessionState getState() {
        return state;
    }

    public Incident getLatestIncident() {
        return incidentService.getLatestIncident();
    }

    public IncidentService getIncidentService() {
        return incidentService;
    }
}