
package com.example.waterwatch.controller;

import com.example.waterwatch.service.UsageService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    private final UsageService usageService;

    public UsageController(UsageService usageService) {
        this.usageService = usageService;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return usageService.getSummary();
    }
}
