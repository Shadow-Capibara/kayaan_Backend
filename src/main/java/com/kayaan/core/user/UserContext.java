package com.kayaan.core.user;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserContext {
    private final HttpServletRequest request;

    public UserContext(HttpServletRequest request) {
        this.request = request;
    }

    public Long getCurrentUserId() {
        String header = request.getHeader("X-User-Id");
        if (header == null || header.isBlank()) {
            return 1L; // mock user id for dev
        }
        try {
            return Long.parseLong(header);
        } catch (NumberFormatException e) {
            return 1L;
        }
    }
}


