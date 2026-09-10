package com.tyelaman.pulsewatch.check;

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