package com.example.template.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "Application health monitoring endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Returns the current health status of the application")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "spring-boot-template");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness check", description = "Returns readiness status for Kubernetes probes")
    public ResponseEntity<Map<String, String>> ready() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "READY");
        return ResponseEntity.ok(status);
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness check", description = "Returns liveness status for Kubernetes probes")
    public ResponseEntity<Map<String, String>> live() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "ALIVE");
        return ResponseEntity.ok(status);
    }
}