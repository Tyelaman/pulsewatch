package com.tyelaman.pulsewatch.controller;

import com.tyelaman.pulsewatch.ai.IncidentSummaryService;
import com.tyelaman.pulsewatch.service.SessionDemoMonitor;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tyelaman.pulsewatch.model.CheckResult;
import com.tyelaman.pulsewatch.model.Incident;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/demo-session")
public class DemoSessionController {

    private static final String SESSION_MONITOR_KEY =
            "sessionDemoMonitor";

    private final IncidentSummaryService summaryService;

    public DemoSessionController(
            IncidentSummaryService summaryService) {

        this.summaryService = summaryService;
    }

    @GetMapping("/status")
    public String getStatus(HttpSession session) {
        SessionDemoMonitor monitor = getOrCreateMonitor(session);

        return monitor.getState().isHealthy() ? "UP" : "DOWN";
    }

    @PostMapping("/fail")
    public String simulateFailure(HttpSession session) {
        SessionDemoMonitor monitor = getOrCreateMonitor(session);

        monitor.getState().simulateFailure();

        return "Demo session is now failing";
    }

    @PostMapping("/recover")
    public String recover(HttpSession session) {
        SessionDemoMonitor monitor = getOrCreateMonitor(session);

        monitor.getState().recover();

        return "Demo session has recovered";
    }

    @GetMapping("/check-result")
    public ResponseEntity<CheckResult> getCheckResult(
            HttpSession session) {

        SessionDemoMonitor monitor = getOrCreateMonitor(session);
        CheckResult result = monitor.checkIfDue();

        if (result == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/incident")
    public ResponseEntity<Incident> getIncident(
            HttpSession session) {

        SessionDemoMonitor monitor = getOrCreateMonitor(session);
        Incident incident = monitor.getLatestIncident();

        if (incident == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(incident);
    }

    private SessionDemoMonitor getOrCreateMonitor(
            HttpSession session) {

        synchronized (session) {
            SessionDemoMonitor monitor = (SessionDemoMonitor)
                    session.getAttribute(SESSION_MONITOR_KEY);

            if (monitor == null) {
                monitor = new SessionDemoMonitor(summaryService);

                session.setAttribute(
                        SESSION_MONITOR_KEY,
                        monitor
                );
            }

            return monitor;
        }
    }
}