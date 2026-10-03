package com.tyelaman.pulsewatch.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoServiceController {

    private final String demoToken;

    private volatile boolean healthy = true;

    public DemoServiceController(
            @Value("${pulsewatch.demo.token}") String demoToken) {

        this.demoToken = demoToken;
    }

    @GetMapping("/target")
    public ResponseEntity<String> getTarget() {
        if (healthy) {
            return ResponseEntity.ok("Demo service is healthy");
        }

        return ResponseEntity
                .internalServerError()
                .body("Demo service is failing");
    }

    @PostMapping("/fail")
    public ResponseEntity<String> fail(
            @RequestHeader(
                    value = "X-Demo-Token",
                    required = false
            ) String providedToken) {

        if (!isAuthorized(providedToken)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Unauthorized demo action");
        }

        healthy = false;

        return ResponseEntity.ok("Demo service is now failing");
    }

    @PostMapping("/recover")
    public ResponseEntity<String> recover(
            @RequestHeader(
                    value = "X-Demo-Token",
                    required = false
            ) String providedToken) {

        if (!isAuthorized(providedToken)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Unauthorized demo action");
        }

        healthy = true;

        return ResponseEntity.ok("Demo service has recovered");
    }

    private boolean isAuthorized(String providedToken) {
        if (demoToken.isBlank() || providedToken == null) {
            return false;
        }

        return MessageDigest.isEqual(
                demoToken.getBytes(StandardCharsets.UTF_8),
                providedToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}