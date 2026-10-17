package com.tyelaman.pulsewatch.model;

import com.tyelaman.pulsewatch.enums.CheckStatus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonitorTest {

    @Test
    void should_track_consecutive_failures_and_reset_on_recovery() {
        Monitor monitor = new Monitor(
                1L,
                "Example API",
                "http://example.com",
                30
        );

        monitor.recordCheck(CheckStatus.DOWN);
        monitor.recordCheck(CheckStatus.DOWN);

        assertEquals(
                CheckStatus.DOWN,
                monitor.getStatus()
        );

        assertEquals(
                2,
                monitor.getConsecutiveFailures()
        );

        monitor.recordCheck(CheckStatus.UP);

        assertEquals(
                CheckStatus.UP,
                monitor.getStatus()
        );

        assertEquals(
                0,
                monitor.getConsecutiveFailures()
        );
    }
}