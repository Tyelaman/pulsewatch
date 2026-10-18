package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.model.Monitor;
import com.tyelaman.pulsewatch.preset.MonitorPresetCatalog;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MonitorServiceImplTest {

    @Test
    void should_create_multiple_monitors_with_unique_ids() {
        MonitorPresetCatalog presetCatalog =
                new MonitorPresetCatalog();

        MonitorService monitorService =
                new MonitorServiceImpl(presetCatalog);

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

        MonitorService monitorService =
                new MonitorServiceImpl(presetCatalog);

        assertThrows(
                IllegalArgumentException.class,
                () -> monitorService.addMonitor("random-api")
        );

        assertEquals(0, monitorService.getMonitors().size());
    }
}