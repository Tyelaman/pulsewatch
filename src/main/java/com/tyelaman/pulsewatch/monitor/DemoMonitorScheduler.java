package com.tyelaman.pulsewatch.monitor;

import com.tyelaman.pulsewatch.check.CheckResult;
import com.tyelaman.pulsewatch.check.HealthCheckService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoMonitorScheduler {

    private static final String DEMO_URL =
            "http://localhost:8080/demo/target";

    private final HealthCheckService healthCheckService;

    private int consecutiveFailures = 0;
    private boolean incidentOpen = false;

    public DemoMonitorScheduler(
            HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
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

        if ("DOWN".equals(result.status())) {
            consecutiveFailures++;

            System.out.println(
                    "Consecutive failures: " + consecutiveFailures
            );

            if (consecutiveFailures >= 3 && !incidentOpen) {
                incidentOpen = true;

                System.out.println(
                        "!!! INCIDENT OPENED: Demo service is DOWN !!!"
                );
            }
        }
        else {
            consecutiveFailures = 0;

            if (incidentOpen) {
                incidentOpen = false;

                System.out.println(
                        "!!! INCIDENT RESOLVED: Demo service recovered !!!"
                );
            }
        }
    }
}