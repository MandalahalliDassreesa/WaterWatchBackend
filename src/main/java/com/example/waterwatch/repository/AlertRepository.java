
package com.example.waterwatch.repository;

import com.example.waterwatch.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findAllByOrderByRecordedAtDesc();

    List<Alert> findAllByDevice_IdAndAlertTypeOrderByRecordedAtDesc(
            Long deviceId,
            String alertType
    );
}
