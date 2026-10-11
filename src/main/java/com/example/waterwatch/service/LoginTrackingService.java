package com.example.waterwatch.service;

import com.example.waterwatch.entity.LoginHistory;
import com.example.waterwatch.repository.LoginHistoryRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginTrackingService {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginTrackingService(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Transactional
    public void recordLogin(String googleId, String name, String email) {
        if (googleId == null || googleId.isBlank()
                || name == null || name.isBlank()
                || email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Google ID, name, and email are required");
        }

        LocalDateTime now = LocalDateTime.now();

        LoginHistory history = loginHistoryRepository
                .findByGoogleId(googleId)
                .orElseGet(LoginHistory::new);

        if (history.getId() == null) {
            history.setGoogleId(googleId);
            history.setFirstLoginAt(now);
        }

        history.setName(name);
        history.setEmail(email);
        history.setLastLoginAt(now);

        loginHistoryRepository.save(history);
    }
}
