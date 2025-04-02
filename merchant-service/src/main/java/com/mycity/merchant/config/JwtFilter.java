package com.mycity.merchant.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtservice;

    public JwtFilter(JwtService jwtservice) {
        this.jwtservice = jwtservice;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // ✅ Skip JWT validation for public endpoints
        if (requestPath.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (token != null) {
            try {
                Claims claims = jwtservice.ValidateToken(token);
                request.setAttribute("email", claims.getSubject());
                request.setAttribute("username", claims.get("username"));
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        }

        // ✅ Continue with request processing
        filterChain.doFilter(request, response);
    }
}
