
package com.example.waterwatch.controller;

import com.example.waterwatch.entity.Alert;
import com.example.waterwatch.repository.AlertRepository;
import com.example.waterwatch.service.LeakDetectionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class LeakAlertController {

    private final LeakDetectionService leakDetectionService;
    private final AlertRepository alertRepository;

    public LeakAlertController(
            LeakDetectionService leakDetectionService,
            AlertRepository alertRepository) {
        this.leakDetectionService = leakDetectionService;
        this.alertRepository = alertRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAlerts() {
        return leakDetectionService.detectPossibleLeaks();
    }

    @PatchMapping("/{id}/resolve")
    public Map<String, Object> resolveAlert(@PathVariable Long id) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alert not found"
                ));

        if (!"RESOLVED".equals(alert.getStatus())) {
            alert.setStatus("RESOLVED");
            alert.setResolvedAt(LocalDateTime.now());
            alertRepository.save(alert);
        }

        return Map.of(
                "id", alert.getId(),
                "status", alert.getStatus(),
                "resolvedAt", alert.getResolvedAt(),
                "message", "Alert resolved successfully"
        );
    }

    @PatchMapping("/resolve-all")
    public Map<String, Object> resolveAllAlerts() {

        List<Alert> alerts = alertRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        int resolvedCount = 0;

        for (Alert alert : alerts) {
            if (!"RESOLVED".equals(alert.getStatus())) {
                alert.setStatus("RESOLVED");
                alert.setResolvedAt(now);
                resolvedCount++;
            }
        }

        alertRepository.saveAll(alerts);

        return Map.of(
                "message", "All active alerts resolved successfully",
                "resolvedCount", resolvedCount
        );
    }
}
