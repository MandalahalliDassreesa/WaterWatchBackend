
package com.example.waterwatch.service;

import com.example.waterwatch.entity.WaterReading;
import com.example.waterwatch.repository.WaterReadingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UsageService {

    private final WaterReadingRepository readingRepository;

    public UsageService(WaterReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    public Map<String, Object> getSummary() {
        List<WaterReading> readings = readingRepository.findAll();

        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();

        double daily = 0;
        double monthly = 0;
        double totalFlow = 0;
        double peakFlow = 0;
        int count = 0;

        for (WaterReading reading : readings) {
            LocalDateTime recordedAt = reading.getRecordedAt();
            double volume = reading.getVolumeLitres();

            if (!recordedAt.isBefore(dayStart)) {
                daily += volume;
            }

            if (!recordedAt.isBefore(monthStart)) {
                monthly += volume;
            }

            totalFlow += reading.getFlowRate();
            peakFlow = Math.max(peakFlow, reading.getFlowRate());
            count++;
        }

        return Map.of(
                "dailyConsumptionLitres", daily,
                "monthlyConsumptionLitres", monthly,
                "averageFlowRate", count == 0 ? 0 : totalFlow / count,
                "peakFlowRate", peakFlow,
                "totalReadings", count
        );
    }
}
