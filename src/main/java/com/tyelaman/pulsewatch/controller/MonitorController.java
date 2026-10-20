package com.tyelaman.pulsewatch.controller;

import com.tyelaman.pulsewatch.model.Monitor;
import com.tyelaman.pulsewatch.service.MonitorService;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitors")
public class MonitorController {

    private final MonitorService monitorService;
    private final String monitorToken;
    public MonitorController(
            MonitorService monitorService,
            @Value("${pulsewatch.demo.token}") String monitorToken) {

        this.monitorService = monitorService;
        this.monitorToken = monitorToken;
    }
    @GetMapping
    public List<Monitor> getMonitors() {
        return monitorService.getMonitors();
    }

    @PostMapping
    public ResponseEntity<?> addMonitor(
            @RequestHeader(
                    value = "X-Demo-Token",
                    required = false
            ) String providedToken,
            @RequestBody(required = false) AddMonitorRequest request) {

        if (!isAuthorized(providedToken)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Unauthorized monitor action");
        }

        if (request == null ||
                request.presetKey() == null ||
                request.presetKey().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Preset key is required");
        }

        try {
            Monitor monitor =
                    monitorService.addMonitor(request.presetKey());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(monitor);
        }
        catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body("Unknown preset key");
        }
    }
    public record AddMonitorRequest(String presetKey) {
    }

    private boolean isAuthorized(String providedToken) {
        if (monitorToken.isBlank() || providedToken == null) {
            return false;
        }

        return MessageDigest.isEqual(
                monitorToken.getBytes(StandardCharsets.UTF_8),
                providedToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}