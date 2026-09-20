package com.tyelaman.pulsewatch.monitor;

import com.tyelaman.pulsewatch.check.CheckResult;
import com.tyelaman.pulsewatch.check.HealthCheckService;
import com.tyelaman.pulsewatch.incident.IncidentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoMonitorScheduler {

    private static final String DEMO_URL =
            "http://localhost:8080/demo/target";

    private final HealthCheckService healthCheckService;
    private final IncidentService incidentService;

    public DemoMonitorScheduler(
            HealthCheckService healthCheckService,
            IncidentService incidentService) {

        this.healthCheckService = healthCheckService;
        this.incidentService = incidentService;
    }

    @Scheduled(fixedRate = 5000)
    public void checkDemoService() {
        CheckResult result = healthCheckService.check(DEMO_URL);

        System.out.println(
                "PulseWatch check: " +
                        result.status() +
                        " | HTTP " +
                        result.statusCode() +
                        " | " +
                        result.responseTimeMs() +
                        " ms"
        );

        incidentService.processCheck(
                "Demo Service",
                result
        );
    }
}