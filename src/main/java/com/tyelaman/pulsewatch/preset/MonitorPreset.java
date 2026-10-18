package com.tyelaman.pulsewatch.preset;

public record MonitorPreset(
        String key,
        String name,
        String url,
        int defaultIntervalSeconds) {
}