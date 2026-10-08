package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.model.CheckResult;

public interface HealthCheckService {

    CheckResult check(String url);
}