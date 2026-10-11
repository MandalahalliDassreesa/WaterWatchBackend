
package com.example.waterwatch.service;

import com.example.waterwatch.entity.Alert;
import com.example.waterwatch.entity.Device;
import com.example.waterwatch.entity.WaterReading;
import com.example.waterwatch.repository.AlertRepository;
import com.example.waterwatch.repository.WaterReadingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LeakDetectionService {

    private final WaterReadingRepository waterReadingRepository;
    private final AlertRepository alertRepository;

    @Value("${waterwatch.leak.flow-threshold:2.0}")
    private double flowThreshold;

    @Value("${waterwatch.leak.duration-minutes:5}")
    private long durationMinutes;

    public LeakDetectionService(
            WaterReadingRepository waterReadingRepository,
            AlertRepository alertRepository) {
        this.waterReadingRepository = waterReadingRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional
    public List<Map<String, Object>> detectPossibleLeaks() {

        LocalDateTime since = LocalDateTime.now().minusHours(24);

        List<WaterReading> readings =
                waterReadingRepository
                        .findByRecordedAtAfterOrderByRecordedAtAsc(since);

        Map<Long, List<WaterReading>> grouped = new HashMap<>();

        for (WaterReading reading : readings) {
            if (reading.getDevice() == null
                    || reading.getDevice().getId() == null) {
                continue;
            }

            Long deviceId = reading.getDevice().getId();

            grouped.computeIfAbsent(deviceId, key -> new ArrayList<>())
                    .add(reading);
        }

        for (Map.Entry<Long, List<WaterReading>> entry : grouped.entrySet()) {

            Long deviceId = entry.getKey();
            List<WaterReading> deviceReadings = entry.getValue();

            deviceReadings.sort(
                    Comparator.comparing(
                            WaterReading::getId,
                            Comparator.nullsFirst(Comparator.naturalOrder())
                    ).reversed()
            );

            if (deviceReadings.size() > 40) {
                deviceReadings = new ArrayList<>(
                        deviceReadings.subList(0, 40)
                );
            }

            deviceReadings.sort(
                    Comparator.comparing(WaterReading::getRecordedAt)
            );

            LocalDateTime highFlowStart = null;
            WaterReading lastHighReading = null;

            for (WaterReading reading : deviceReadings) {

                if (reading.getFlowRate() >= flowThreshold) {

                    if (highFlowStart == null) {
                        highFlowStart = reading.getRecordedAt();
                    }

                    lastHighReading = reading;

                    long elapsed = Duration.between(
                            highFlowStart,
                            reading.getRecordedAt()
                    ).toMinutes();

                    if (elapsed >= durationMinutes) {

                        List<Alert> existing =
                                alertRepository.findAllByDevice_IdAndAlertTypeOrderByRecordedAtDesc(
                                        deviceId,
                                        "POSSIBLE_LEAK"
                                );

                        boolean hasActiveAlert = existing.stream()
                                .anyMatch(alert -> !"RESOLVED".equals(alert.getStatus()));

                        boolean shouldCreateAlert = existing.isEmpty() || !hasActiveAlert;



                        if (shouldCreateAlert) {

                            Device device = reading.getDevice();

                            Alert alert = new Alert();
                            alert.setDevice(device);
                            alert.setAlertType("POSSIBLE_LEAK");
                            alert.setMessage(
                                    "Possible leak: prolonged high water flow"
                            );
                            alert.setStatus("REQUIRES_INSPECTION");
                            alert.setFlowRate(
                                    lastHighReading.getFlowRate()
                            );
                            alert.setDurationMinutes(elapsed);
                            alert.setRecordedAt(
                                    LocalDateTime.now()
                            );

                            alertRepository.save(alert);
                        }

                        break;

                    }

                } else {
                    highFlowStart = null;
                    lastHighReading = null;
                }
            }
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (Alert alert : alertRepository.findAllByOrderByRecordedAtDesc()) {

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("id", alert.getId());
            item.put("deviceId", alert.getDevice().getId());
            item.put("deviceName", alert.getDevice().getDeviceName());
            item.put("location", alert.getDevice().getLocation());
            item.put("message", alert.getMessage());
            item.put("status", alert.getStatus());
            item.put("flowRate", alert.getFlowRate());
            item.put("recordedAt", alert.getRecordedAt());
            item.put("durationMinutes", alert.getDurationMinutes());

            results.add(item);
        }

        return results;
    }
}
