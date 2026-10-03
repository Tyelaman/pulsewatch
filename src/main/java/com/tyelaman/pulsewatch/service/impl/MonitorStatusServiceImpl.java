package com.tyelaman.pulsewatch.service.impl;

import com.tyelaman.pulsewatch.model.CheckResult;
import com.tyelaman.pulsewatch.service.MonitorStatusService;

import org.springframework.stereotype.Service;

@Service
public class MonitorStatusServiceImpl implements MonitorStatusService {

    private volatile CheckResult latestResult = null;

    @Override
    public void updateLatestResult(CheckResult result) {
        latestResult = result;
    }

    @Override
    public CheckResult getLatestResult() {
        return latestResult;
    }
}