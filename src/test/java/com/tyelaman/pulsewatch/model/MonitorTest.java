package com.tyelaman.pulsewatch.model;

import com.tyelaman.pulsewatch.enums.CheckStatus;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void should_only_check_when_interval_has_passed() {
        Monitor monitor = new Monitor(
                1L,
                "JSONPlaceholder",
                "https://jsonplaceholder.typicode.com/posts/1",
                30
        );

        assertTrue(monitor.isCheckDue(Instant.now()));

        monitor.recordCheck(CheckStatus.UP);

        Instant lastChecked = monitor.getLastCheckedAt();

        assertFalse(monitor.isCheckDue(lastChecked.plusSeconds(10)));
        assertFalse(monitor.isCheckDue(lastChecked.plusSeconds(29)));
        assertTrue(monitor.isCheckDue(lastChecked.plusSeconds(30)));
        assertTrue(monitor.isCheckDue(lastChecked.plusSeconds(35)));
    }
}