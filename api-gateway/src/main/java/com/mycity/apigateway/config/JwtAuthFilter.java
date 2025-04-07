package com.mycity.apigateway.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class JwtAuthFilter implements GlobalFilter {

    private final JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    // Role to downstream service name mapping
    private static final Map<String, String> ROLE_SERVICE_MAPPING = Map.of(
        "ADMIN", "admin-service",
        "USER", "user-service"
    );

    // Allow login and register without token
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/auth/user/login",
        "/auth/user/register"
    );

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getURI().getPath();
    System.out.println(requestPath +"request path for user service");
    
        // Allow public endpoints
        if (PUBLIC_ENDPOINTS.stream().anyMatch(requestPath::startsWith)) {
            return chain.filter(exchange);
        }

        // Check for Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return handleUnauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            if (!jwtService.validateToken(token)) {
                return handleUnauthorized(exchange, "Invalid or expired token");
            }
        } catch (Exception e) {
            return handleUnauthorized(exchange, "Token validation failed: " + e.getMessage());
        }

        // Extract user info
        String userId = jwtService.extractUserId(token);
        String userRole = jwtService.extractRole(token);
        logger.info("Authenticated request - UserID: {}, Role: {}", userId, userRole);

        if (!ROLE_SERVICE_MAPPING.containsKey(userRole.toUpperCase())) {
            return handleUnauthorized(exchange, "Unauthorized role: " + userRole);
        }

        // Add headers for downstream services (including Authorization)
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-User-Id", userId)
            .header("X-User-Role", userRole)
            .header(HttpHeaders.AUTHORIZATION, authHeader) // 🔐 KEEP token for downstream
            .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        logger.warn("Unauthorized access: {}", message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
