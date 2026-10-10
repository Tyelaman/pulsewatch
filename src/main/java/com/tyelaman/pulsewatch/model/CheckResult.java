package com.tyelaman.pulsewatch.model;

import java.time.Instant;

import com.tyelaman.pulsewatch.enums.CheckStatus;

public record CheckResult(
        String url,
        CheckStatus status,
        Integer statusCode,
        long responseTimeMs,
        Instant checkedAt,
        String error
) {
}