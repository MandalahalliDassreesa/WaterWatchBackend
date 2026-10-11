
package com.example.waterwatch.controller;

import com.example.waterwatch.entity.Device;
import com.example.waterwatch.entity.WaterReading;
import com.example.waterwatch.repository.DeviceRepository;
import com.example.waterwatch.repository.WaterReadingRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/readings")
public class SimulationController {

    private final DeviceRepository deviceRepository;
    private final WaterReadingRepository readingRepository;

    public SimulationController(
            DeviceRepository deviceRepository,
            WaterReadingRepository readingRepository) {
        this.deviceRepository = deviceRepository;
        this.readingRepository = readingRepository;
    }

    @PostMapping("/simulate")
    public List<WaterReading> simulateReadings(
            @RequestParam Long deviceId,
            @RequestParam(defaultValue = "40") int count,
            @RequestParam(defaultValue = "false") boolean simulateLeak) {

        if (count < 1 || count > 120) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Count must be between 1 and 120"
            );
        }

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Device not found: " + deviceId
                ));

        List<WaterReading> readings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {

            boolean highFlow = simulateLeak
                    && i >= count - Math.min(35, count);

            double flowRate = highFlow
                    ? ThreadLocalRandom.current().nextDouble(2.2, 3.1)
                    : ThreadLocalRandom.current().nextDouble(0.2, 1.6);

            flowRate = Math.round(flowRate * 100.0) / 100.0;

            WaterReading reading = new WaterReading();

            reading.setDevice(device);
            reading.setFlowRate(flowRate);
            reading.setIntervalMinutes(1.0);
            reading.setVolumeLitres(flowRate);
            reading.setRecordedAt(
                    now.minusMinutes(count - 1L - i)
            );

            readings.add(readingRepository.save(reading));
        }

        return readings;
    }
}
