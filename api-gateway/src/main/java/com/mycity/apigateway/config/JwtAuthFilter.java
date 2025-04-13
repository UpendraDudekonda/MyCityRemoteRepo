package com.mycity.apigateway.config;

import java.nio.charset.StandardCharsets;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Allow unauthenticated access to /auth/** (login, registration, etc.)
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        // Validate the JWT token
        if (!jwtService.validateToken(token)) {
            return unauthorized(exchange, "Invalid or expired JWT Token");
        }

        // Extract claims from the token
        Claims claims = jwtService.extractAllClaims(token);

        // Get role and user ID from the claims
        String role = claims.get("role", String.class);
        Long userId = claims.get("userId", Long.class);

        // Role-based access control
        if (role != null) {
            if ("USER".equals(role) && !path.startsWith("/user/")) {
                return unauthorized(exchange, "Access Denied: USER role cannot access this route");
            }
            if ("MERCHANT".equals(role) && !path.startsWith("/merchant/")) {
                return unauthorized(exchange, "Access Denied: MERCHANT role cannot access this route");
            }
            if ("ADMIN".equals(role) && !path.startsWith("/admin/")) {
                return unauthorized(exchange, "Access Denied: ADMIN role cannot access this route");
            }
        } else {
            return unauthorized(exchange, "Invalid role in token");
        }

        return chain.filter(exchange); // Continue the filter chain if everything is valid
    }

    // Helper method to handle unauthorized access responses
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        String responseMessage = String.format("{\"error\": \"%s\"}", message);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseMessage.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public int getOrder() {
        return -1; // High precedence to ensure it runs before other filters
    }
}
