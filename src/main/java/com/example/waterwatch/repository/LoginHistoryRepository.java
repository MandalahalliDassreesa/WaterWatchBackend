
package com.example.waterwatch.repository;

import com.example.waterwatch.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoginHistoryRepository
        extends JpaRepository<LoginHistory, Long> {

    Optional<LoginHistory> findByGoogleId(String googleId);
}
