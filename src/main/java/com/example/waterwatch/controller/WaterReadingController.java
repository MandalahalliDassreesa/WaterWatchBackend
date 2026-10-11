
package com.example.waterwatch.controller;

import com.example.waterwatch.entity.Device;
import com.example.waterwatch.entity.WaterReading;
import com.example.waterwatch.repository.DeviceRepository;
import com.example.waterwatch.repository.WaterReadingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/readings")
public class WaterReadingController {

    private final WaterReadingRepository readingRepository;
    private final DeviceRepository deviceRepository;

    public WaterReadingController(
            WaterReadingRepository readingRepository,
            DeviceRepository deviceRepository) {
        this.readingRepository = readingRepository;
        this.deviceRepository = deviceRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaterReading addReading(@RequestBody Map<String, Object> data) {
        Long deviceId = Long.valueOf(data.get("deviceId").toString());
        double flowRate = Double.parseDouble(data.get("flowRate").toString());
        double intervalMinutes = Double.parseDouble(
                data.get("intervalMinutes").toString());

        if (!Double.isFinite(flowRate) || flowRate < 0
                || !Double.isFinite(intervalMinutes)
                || intervalMinutes <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Flow rate must be non-negative and interval must be positive"
            );
        }

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Device not found"));

        WaterReading reading = new WaterReading();
        reading.setDevice(device);
        reading.setFlowRate(flowRate);
        reading.setIntervalMinutes(intervalMinutes);
        reading.setVolumeLitres(flowRate * intervalMinutes);

        return readingRepository.save(reading);
    }

    @GetMapping
    public List<WaterReading> getReadings(
            @RequestParam(required = false) Long deviceId) {
        if (deviceId != null) {
            return readingRepository
                    .findByDeviceIdOrderByRecordedAtDesc(deviceId);
        }
        return readingRepository.findAll();
    }
}
