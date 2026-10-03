package com.tyelaman.pulsewatch.model;

import java.time.Instant;

public record CheckResult(
        String url,
        String status,
        Integer statusCode,
        long responseTimeMs,
        Instant checkedAt,
        String error
) {
}