package com.tyelaman.pulsewatch.monitor;

import com.tyelaman.pulsewatch.check.CheckResult;
import org.springframework.stereotype.Service;

@Service
public class MonitorStatusService {

    private volatile CheckResult latestResult = null;

    public void updateStatus(CheckResult result) {
        latestResult = result;
    }

    public CheckResult getLatestResult() {
        return latestResult;
    }
}