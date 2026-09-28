package com.example.examseatplan.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Allow access to login, static resources, error pages, and REST APIs if needed (or protect web pages)
        if (uri.startsWith("/login") || uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/error")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            return true;
        }

        // Not logged in -> redirect to login
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
