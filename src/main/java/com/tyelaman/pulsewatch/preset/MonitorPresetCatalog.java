package com.tyelaman.pulsewatch.preset;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MonitorPresetCatalog {

    private final List<MonitorPreset> presets = List.of(
            new MonitorPreset(
                    "jsonplaceholder",
                    "JSONPlaceholder",
                    "https://jsonplaceholder.typicode.com/posts/1",
                    30
            ),
            new MonitorPreset(
                    "httpbin-healthy",
                    "HTTPBin Healthy",
                    "https://httpbin.org/status/200",
                    30
            ),
            new MonitorPreset(
                    "httpbin-failure",
                    "HTTPBin Failure",
                    "https://httpbin.org/status/503",
                    30
            ),
            new MonitorPreset(
                    "httpbin-slow",
                    "HTTPBin Slow",
                    "https://httpbin.org/delay/3",
                    30
            )
    );

    public List<MonitorPreset> getPresets() {
        return presets;
    }
    public Optional<MonitorPreset> findByKey(String key) {
        return presets.stream()
                .filter(preset -> preset.key().equals(key))
                .findFirst();
    }
}