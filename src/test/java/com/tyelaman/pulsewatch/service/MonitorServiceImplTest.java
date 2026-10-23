package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.model.Monitor;
import com.tyelaman.pulsewatch.preset.MonitorPresetCatalog;
import com.tyelaman.pulsewatch.enums.CheckStatus;
import com.tyelaman.pulsewatch.model.CheckResult;
import com.tyelaman.pulsewatch.model.Monitor;
import java.time.Instant;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MonitorServiceImplTest {

    @Test
    void should_create_multiple_monitors_with_unique_ids() {
        MonitorPresetCatalog presetCatalog =
                new MonitorPresetCatalog();

        HealthCheckService healthCheckService =
                Mockito.mock(HealthCheckService.class);

        MonitorService monitorService =
                new MonitorServiceImpl(
                        presetCatalog,
                        healthCheckService
                );

        Monitor firstMonitor =
                monitorService.addMonitor("jsonplaceholder");

        Monitor secondMonitor =
                monitorService.addMonitor("httpbin-healthy");

        List<Monitor> monitors = monitorService.getMonitors();

        assertEquals(2, monitors.size());

        assertNotEquals(
                firstMonitor.getId(),
                secondMonitor.getId()
        );

        assertEquals(
                "JSONPlaceholder",
                firstMonitor.getName()
        );

        assertEquals(
                "HTTPBin Healthy",
                secondMonitor.getName()
        );
    }
    @Test
    void should_reject_unknown_preset() {
        MonitorPresetCatalog presetCatalog =
                new MonitorPresetCatalog();

        HealthCheckService healthCheckService =
                Mockito.mock(HealthCheckService.class);

        MonitorService monitorService =
                new MonitorServiceImpl(
                        presetCatalog,
                        healthCheckService
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> monitorService.addMonitor("random-api")
        );

        assertEquals(0, monitorService.getMonitors().size());
    }

    @Test
    void should_reject_more_than_five_monitors() {
        MonitorPresetCatalog presetCatalog =
                new MonitorPresetCatalog();

        HealthCheckService healthCheckService =
                Mockito.mock(HealthCheckService.class);

        MonitorService monitorService =
                new MonitorServiceImpl(
                        presetCatalog,
                        healthCheckService
                );

        for (int i = 0; i < 5; i++) {
            monitorService.addMonitor("jsonplaceholder");
        }

        assertEquals(5, monitorService.getMonitors().size());

        assertThrows(
                IllegalStateException.class,
                () -> monitorService.addMonitor("jsonplaceholder")
        );

        assertEquals(5, monitorService.getMonitors().size());
    }

    @Test
    void should_update_monitor_status_after_successful_check() {
        MonitorPresetCatalog presetCatalog =
                new MonitorPresetCatalog();

        HealthCheckService healthCheckService =
                Mockito.mock(HealthCheckService.class);

        MonitorService monitorService =
                new MonitorServiceImpl(presetCatalog, healthCheckService);

        Monitor monitor = monitorService.addMonitor("jsonplaceholder");

        assertEquals(CheckStatus.UNKNOWN, monitor.getStatus());

        CheckResult successfulResult = new CheckResult(
                monitor.getUrl(),
                CheckStatus.UP,
                200,
                45L,
                Instant.now(),
                null
        );

        Mockito.when(healthCheckService.check(monitor.getUrl()))
                .thenReturn(successfulResult);

        Monitor updatedMonitor =
                monitorService.checkMonitor(monitor.getId());

        assertEquals(CheckStatus.UP, updatedMonitor.getStatus());
        assertEquals(0, updatedMonitor.getConsecutiveFailures());

        Mockito.verify(healthCheckService)
                .check(monitor.getUrl());
    }

    @Test
    void should_record_failure_after_unsuccessful_check() {
        MonitorPresetCatalog presetCatalog =
                new MonitorPresetCatalog();

        HealthCheckService healthCheckService =
                Mockito.mock(HealthCheckService.class);

        MonitorService monitorService =
                new MonitorServiceImpl(presetCatalog, healthCheckService);

        Monitor monitor = monitorService.addMonitor("httpbin-failure");

        CheckResult failedResult = new CheckResult(
                monitor.getUrl(),
                CheckStatus.DOWN,
                503,
                120L,
                Instant.now(),
                null
        );

        Mockito.when(healthCheckService.check(monitor.getUrl()))
                .thenReturn(failedResult);

        Monitor updatedMonitor =
                monitorService.checkMonitor(monitor.getId());

        assertEquals(CheckStatus.DOWN, updatedMonitor.getStatus());
        assertEquals(1, updatedMonitor.getConsecutiveFailures());

        Mockito.verify(healthCheckService)
                .check(monitor.getUrl());
    }

    @Test
    void should_reset_failures_after_recovery() {
        MonitorPresetCatalog presetCatalog =
                new MonitorPresetCatalog();

        HealthCheckService healthCheckService =
                Mockito.mock(HealthCheckService.class);

        MonitorService monitorService =
                new MonitorServiceImpl(presetCatalog, healthCheckService);

        Monitor monitor = monitorService.addMonitor("jsonplaceholder");

        CheckResult failedResult = new CheckResult(
                monitor.getUrl(),
                CheckStatus.DOWN,
                503,
                120L,
                Instant.now(),
                null
        );

        CheckResult recoveredResult = new CheckResult(
                monitor.getUrl(),
                CheckStatus.UP,
                200,
                45L,
                Instant.now(),
                null
        );

        Mockito.when(healthCheckService.check(monitor.getUrl()))
                .thenReturn(failedResult, failedResult, recoveredResult);

        monitorService.checkMonitor(monitor.getId());
        monitorService.checkMonitor(monitor.getId());

        assertEquals(CheckStatus.DOWN, monitor.getStatus());
        assertEquals(2, monitor.getConsecutiveFailures());

        monitorService.checkMonitor(monitor.getId());

        assertEquals(CheckStatus.UP, monitor.getStatus());
        assertEquals(0, monitor.getConsecutiveFailures());

        Mockito.verify(healthCheckService, Mockito.times(3))
                .check(monitor.getUrl());
    }
}