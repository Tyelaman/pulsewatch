package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.model.Monitor;
import com.tyelaman.pulsewatch.preset.MonitorPreset;
import com.tyelaman.pulsewatch.preset.MonitorPresetCatalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.context.annotation.SessionScope;
import org.springframework.stereotype.Service;

@Service
@SessionScope
public class MonitorServiceImpl implements MonitorService {

    private final MonitorPresetCatalog presetCatalog;
    private final List<Monitor> monitors = new ArrayList<>();
    private long nextId = 1L;
    private static final int MAX_MONITORS_PER_SESSION = 5;

    public MonitorServiceImpl(MonitorPresetCatalog presetCatalog) {
        this.presetCatalog = presetCatalog;
    }

    @Override
    public synchronized Monitor addMonitor(String presetKey) {
        if (monitors.size() >= MAX_MONITORS_PER_SESSION) {
            throw new IllegalStateException(
                    "Maximum of 5 monitors per session reached"
            );
        }
        MonitorPreset preset = presetCatalog.findByKey(presetKey)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unknown preset: " + presetKey
                        )
                );

        Monitor monitor = new Monitor(
                nextId++,
                preset.name(),
                preset.url(),
                preset.defaultIntervalSeconds()
        );

        monitors.add(monitor);
        return monitor;
    }

    @Override
    public synchronized List<Monitor> getMonitors() {
        return List.copyOf(monitors);
    }
}