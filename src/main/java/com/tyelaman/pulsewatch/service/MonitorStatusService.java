package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.model.CheckResult;

public interface MonitorStatusService {

    void updateLatestResult(CheckResult result);

    CheckResult getLatestResult();
}