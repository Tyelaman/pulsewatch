package com.tyelaman.pulsewatch.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoServiceController {

    private volatile boolean healthy = true;

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
    public String fail() {
        healthy = false;
        return "Demo service is now failing";
    }

    @PostMapping("/recover")
    public String recover() {
        healthy = true;
        return "Demo service has recovered";
    }
}