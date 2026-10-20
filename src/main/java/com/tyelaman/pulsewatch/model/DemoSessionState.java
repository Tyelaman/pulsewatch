package com.tyelaman.pulsewatch.model;

public class DemoSessionState {

    private boolean healthy = true;

    public synchronized boolean isHealthy() {
        return healthy;
    }

    public synchronized void simulateFailure() {
        healthy = false;
    }

    public synchronized void recover() {
        healthy = true;
    }
}