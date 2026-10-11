
package com.example.waterwatch.config;

import com.example.waterwatch.service.LoginTrackingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginTrackingService loginTrackingService;

    public LoginSuccessHandler(LoginTrackingService loginTrackingService) {
        this.loginTrackingService = loginTrackingService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        if (authentication.getPrincipal() instanceof OAuth2User user) {
            String googleId = user.getAttribute("sub");
            String name = user.getAttribute("name");
            String email = user.getAttribute("email");

            loginTrackingService.recordLogin(googleId, name, email);
        }

        response.sendRedirect("https://water-watch-zeta.vercel.app");
    }
}