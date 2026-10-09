package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.model.CheckResult;
import com.tyelaman.pulsewatch.model.Incident;

public interface IncidentService {

    void processCheck(String serviceName, CheckResult result);

    Incident getLatestIncident();
}