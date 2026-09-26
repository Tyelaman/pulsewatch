package com.tyelaman.pulsewatch.monitor;

import com.tyelaman.pulsewatch.check.CheckResult;
import com.tyelaman.pulsewatch.check.HealthCheckService;
import com.tyelaman.pulsewatch.incident.IncidentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoMonitorScheduler {

    private final String demoUrl;

    private final HealthCheckService healthCheckService;
    private final IncidentService incidentService;
    private final MonitorStatusService monitorStatusService;

    public DemoMonitorScheduler(
            @Value("${pulsewatch.demo.url}") String demoUrl,
            HealthCheckService healthCheckService,
            IncidentService incidentService,
            MonitorStatusService monitorStatusService) {

        this.demoUrl = demoUrl;
        this.healthCheckService = healthCheckService;
        this.incidentService = incidentService;
        this.monitorStatusService = monitorStatusService;
    }

    @Scheduled(fixedRate = 5000)
    public void checkDemoService() {
        CheckResult result = healthCheckService.check(demoUrl);

        monitorStatusService.updateStatus(result);

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