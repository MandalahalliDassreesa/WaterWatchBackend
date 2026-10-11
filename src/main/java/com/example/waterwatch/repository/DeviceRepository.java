
package com.example.waterwatch.repository;

import com.example.waterwatch.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
