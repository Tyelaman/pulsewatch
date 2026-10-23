package com.tyelaman.pulsewatch.controller;

import com.tyelaman.pulsewatch.model.Monitor;
import com.tyelaman.pulsewatch.service.MonitorService;

import java.util.List;

import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<?> addMonitor(
            @RequestBody(required = false) AddMonitorRequest request) {

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
        catch (IllegalStateException exception) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Maximum of 5 monitors per session reached");
        }
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<?> checkMonitor(@PathVariable long id) {
        try {
            Monitor monitor = monitorService.checkMonitor(id);

            return ResponseEntity.ok(monitor);
        }
        catch (NoSuchElementException exception) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Monitor not found: " + id);
        }
    }

    public record AddMonitorRequest(String presetKey) {
    }
}