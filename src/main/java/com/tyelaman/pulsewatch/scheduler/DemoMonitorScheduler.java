package com.tyelaman.pulsewatch.scheduler;

import com.tyelaman.pulsewatch.service.impl.HealthCheckServiceImpl;
import com.tyelaman.pulsewatch.incident.IncidentService;
import com.tyelaman.pulsewatch.model.CheckResult;
import com.tyelaman.pulsewatch.service.MonitorStatusService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoMonitorScheduler {

    private final String demoUrl;

    private final HealthCheckServiceImpl healthCheckServiceImpl;
    private final IncidentService incidentService;
    private final MonitorStatusService monitorStatusService;

    public DemoMonitorScheduler(
            @Value("${pulsewatch.demo.url}") String demoUrl,
            HealthCheckServiceImpl healthCheckServiceImpl,
            IncidentService incidentService,
            MonitorStatusService monitorStatusService) {

        this.demoUrl = demoUrl;
        this.healthCheckServiceImpl = healthCheckServiceImpl;
        this.incidentService = incidentService;
        this.monitorStatusService = monitorStatusService;
    }

    @Scheduled(fixedRate = 5000)
    public void checkDemoService() {
        CheckResult result = healthCheckServiceImpl.check(demoUrl);

        monitorStatusService.updateLatestResult(result);

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