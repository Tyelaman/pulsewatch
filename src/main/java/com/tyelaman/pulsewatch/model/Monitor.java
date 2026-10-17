package com.tyelaman.pulsewatch.model;

import com.tyelaman.pulsewatch.enums.CheckStatus;

public class Monitor {

    private final Long id;
    private final String name;
    private final String url;
    private final int intervalSeconds;

    private CheckStatus status;
    private int consecutiveFailures;

    public Monitor(
            Long id,
            String name,
            String url,
            int intervalSeconds) {

        this.id = id;
        this.name = name;
        this.url = url;
        this.intervalSeconds = intervalSeconds;
        this.status = CheckStatus.UNKNOWN;
        this.consecutiveFailures = 0;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public CheckStatus getStatus() {
        return status;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void recordCheck(CheckStatus checkStatus) {
        status = checkStatus;

        if (checkStatus == CheckStatus.DOWN) {
            consecutiveFailures++;
        }
        else {
            consecutiveFailures = 0;
        }
    }
}