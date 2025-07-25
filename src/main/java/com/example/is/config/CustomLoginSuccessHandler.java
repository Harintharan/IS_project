package com.example.is.config;

import com.example.is.service.ActiveUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ActiveUserService activeUserService;

    public CustomLoginSuccessHandler(ActiveUserService activeUserService) {
        this.activeUserService = activeUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        activeUserService.login(username); // ✅ Register as active user
        response.sendRedirect("/chat"); // Redirect to chat page
    }
}
