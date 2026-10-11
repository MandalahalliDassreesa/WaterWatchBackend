
package com.example.waterwatch.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_readings")
public class WaterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private double flowRate;

    @Column(nullable = false)
    private double intervalMinutes;

    @Column(nullable = false)
    private double volumeLitres;

    @Column(nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    public WaterReading() {
    }

    public Long getId() {
        return id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public double getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(double flowRate) {
        this.flowRate = flowRate;
    }

    public double getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(double intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public double getVolumeLitres() {
        return volumeLitres;
    }

    public void setVolumeLitres(double volumeLitres) {
        this.volumeLitres = volumeLitres;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
