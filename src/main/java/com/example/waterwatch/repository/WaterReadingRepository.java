
package com.example.waterwatch.repository;

import com.example.waterwatch.entity.WaterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaterReadingRepository
        extends JpaRepository<WaterReading, Long> {

    List<WaterReading> findByRecordedAtAfterOrderByRecordedAtAsc(
            java.time.LocalDateTime time);

    List<WaterReading> findByDeviceIdOrderByRecordedAtDesc(Long deviceId);
}
