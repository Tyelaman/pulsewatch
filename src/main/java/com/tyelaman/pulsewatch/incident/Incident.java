package com.tyelaman.pulsewatch.incident;

import java.time.Instant;

public class Incident {

    private final String serviceName;
    private final String url;
    private String status;
    private final Instant startedAt;
    private Instant resolvedAt;
    private Integer lastStatusCode;
    private int failureCount;
    private String aiSummary;

    public Incident(
            String serviceName,
            String url,
            Instant startedAt,
            Integer lastStatusCode,
            int failureCount) {

        this.serviceName = serviceName;
        this.url = url;
        this.status = "OPEN";
        this.startedAt = startedAt;
        this.resolvedAt = null;
        this.lastStatusCode = lastStatusCode;
        this.failureCount = failureCount;
        this.aiSummary = null;
    }

    public void recordFailure(Integer statusCode) {
        lastStatusCode = statusCode;
        failureCount++;
    }

    public void resolve(Instant resolvedAt) {
        status = "RESOLVED";
        this.resolvedAt = resolvedAt;
    }

    public boolean isOpen() {
        return "OPEN".equals(status);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public Integer getLastStatusCode() {
        return lastStatusCode;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }
}