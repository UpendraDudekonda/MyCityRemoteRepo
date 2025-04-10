package com.mycity.admin.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip JWT validation for public endpoints
        if (requestPath.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        String header = request.getHeader("Authorization");

        // Check if Authorization header is present and has the Bearer prefix
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        // If token exists, validate it
        if (token != null) {
            try {
                Claims claims = jwtService.validateToken(token); // Assuming validateToken returns Claims if valid
                request.setAttribute("email", claims.getSubject());
                request.setAttribute("username", claims.get("username"));
            } catch (Exception e) {
                logger.error("Invalid JWT token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return; // Stop further processing if token is invalid
            }
        }

        // Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
