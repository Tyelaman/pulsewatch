package com.tyelaman.pulsewatch.controller;

import com.tyelaman.pulsewatch.incident.IncidentService;
import com.tyelaman.pulsewatch.model.Incident;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(
            IncidentService incidentService) {

        this.incidentService = incidentService;
    }

    @GetMapping("/latest")
    public ResponseEntity<Incident> getLatestIncident() {
        Incident incident = incidentService.getLatestIncident();

        if (incident == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(incident);
    }
}