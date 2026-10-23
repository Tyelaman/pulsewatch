package com.tyelaman.pulsewatch.service;

import com.tyelaman.pulsewatch.model.Monitor;

import java.util.List;

public interface MonitorService {

    Monitor addMonitor(String presetKey);

    List<Monitor> getMonitors();

    Monitor checkMonitor(long monitorId);
}