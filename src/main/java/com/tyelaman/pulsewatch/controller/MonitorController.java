package com.tyelaman.pulsewatch.controller;

import com.tyelaman.pulsewatch.model.Monitor;
import com.tyelaman.pulsewatch.service.MonitorService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitors")
public class MonitorController {

    private final MonitorService monitorService;

    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping
    public List<Monitor> getMonitors() {
        return monitorService.getMonitors();
    }
}