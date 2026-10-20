package com.tyelaman.pulsewatch.ai;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class AiRequestLimiter {

    private static final int MAX_REQUESTS_PER_HOUR = 20;
    private static final Duration WINDOW = Duration.ofHours(1);

    private Instant windowStartedAt = Instant.now();
    private int requestCount = 0;

    public synchronized boolean tryAcquire() {
        Instant now = Instant.now();

        if (!now.isBefore(windowStartedAt.plus(WINDOW))) {
            windowStartedAt = now;
            requestCount = 0;
        }

        if (requestCount >= MAX_REQUESTS_PER_HOUR) {
            return false;
        }

        requestCount++;
        return true;
    }
}