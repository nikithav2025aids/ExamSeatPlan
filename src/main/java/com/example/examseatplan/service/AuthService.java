package com.example.examseatplan.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // Default admin credentials for examination administrator
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return ADMIN_USERNAME.trim().equalsIgnoreCase(username.trim()) &&
                ADMIN_PASSWORD.equals(password);
    }
}