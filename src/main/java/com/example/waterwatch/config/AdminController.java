package com.example.waterwatch.config;

import com.example.waterwatch.entity.LoginHistory;
import com.example.waterwatch.repository.LoginHistoryRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final LoginHistoryRepository loginHistoryRepository;

    @Value("${waterwatch.admin.email:}")
    private String adminEmail;

    public AdminController(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!(authentication.getPrincipal() instanceof OAuth2User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String email = user.getAttribute("email");
        Boolean emailVerified = user.getAttribute("email_verified");

        if (adminEmail == null || adminEmail.isBlank()
                || email == null
                || !Boolean.TRUE.equals(emailVerified)
                || !email.trim().equalsIgnoreCase(adminEmail.trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Map<String, Object>> users = loginHistoryRepository.findAll()
                .stream()
                .map(LoginHistory::getId)
                .map(id -> loginHistoryRepository.findById(id).orElseThrow())
                .map(record -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", record.getId());
                    item.put("name", record.getName());
                    item.put("email", record.getEmail());
                    item.put("firstLoginAt", record.getFirstLoginAt());
                    item.put("lastLoginAt", record.getLastLoginAt());
                    return item;
                })
                .toList();

        return ResponseEntity.ok(users);
    }
}