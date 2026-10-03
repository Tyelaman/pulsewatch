package com.tyelaman.pulsewatch.controller;

import com.tyelaman.pulsewatch.model.CheckResult;
import com.tyelaman.pulsewatch.service.MonitorStatusService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class MonitorStatusController {

    private final MonitorStatusService monitorStatusService;

    public MonitorStatusController(
            MonitorStatusService monitorStatusService) {

        this.monitorStatusService = monitorStatusService;
    }

    @GetMapping
    public ResponseEntity<CheckResult> getStatus() {
        CheckResult result = monitorStatusService.getLatestResult();

        if (result == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }
}