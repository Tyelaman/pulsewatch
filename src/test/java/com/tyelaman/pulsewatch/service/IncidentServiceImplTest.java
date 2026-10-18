package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.ai.IncidentSummaryService;
import com.tyelaman.pulsewatch.enums.CheckStatus;
import com.tyelaman.pulsewatch.model.CheckResult;

import com.tyelaman.pulsewatch.enums.IncidentStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IncidentServiceImplTest {

    @Test
    void should_not_open_incident_after_two_failures() {
        IncidentSummaryService summaryService =
                Mockito.mock(IncidentSummaryService.class);

        IncidentServiceImpl incidentService =
                new IncidentServiceImpl(summaryService);

        CheckResult firstFailure = new CheckResult(
                "http://example.com",
                CheckStatus.DOWN,
                500,
                100,
                Instant.now(),
                null
        );

        CheckResult secondFailure = new CheckResult(
                "http://example.com",
                CheckStatus.DOWN,
                500,
                120,
                Instant.now(),
                null
        );

        incidentService.processCheck(
                "Example Service",
                firstFailure
        );

        incidentService.processCheck(
                "Example Service",
                secondFailure
        );

        assertNull(incidentService.getLatestIncident());
    }
    @Test
    void should_open_incident_after_three_consecutive_failures() {
        IncidentSummaryService summaryService =
                Mockito.mock(IncidentSummaryService.class);

        IncidentServiceImpl incidentService =
                new IncidentServiceImpl(summaryService);

        CheckResult failure = new CheckResult(
                "http://example.com",
                CheckStatus.DOWN,
                500,
                100,
                Instant.now(),
                null
        );

        incidentService.processCheck("Example Service", failure);
        incidentService.processCheck("Example Service", failure);
        incidentService.processCheck("Example Service", failure);

        assertNotNull(incidentService.getLatestIncident());
        assertEquals(
                3,
                incidentService.getLatestIncident().getFailureCount()
        );
    }

    @Test
    void should_resolve_incident_when_service_recovers() {
        IncidentSummaryService summaryService =
                Mockito.mock(IncidentSummaryService.class);

        IncidentServiceImpl incidentService =
                new IncidentServiceImpl(summaryService);

        CheckResult failure = new CheckResult(
                "http://example.com",
                CheckStatus.DOWN,
                500,
                100,
                Instant.now(),
                null
        );

        incidentService.processCheck("Example Service", failure);
        incidentService.processCheck("Example Service", failure);
        incidentService.processCheck("Example Service", failure);

        CheckResult recovery = new CheckResult(
                "http://example.com",
                CheckStatus.UP,
                200,
                80,
                Instant.now(),
                null
        );

        incidentService.processCheck("Example Service", recovery);

        assertEquals(
                IncidentStatus.RESOLVED,
                incidentService.getLatestIncident().getStatus()
        );
    }
}