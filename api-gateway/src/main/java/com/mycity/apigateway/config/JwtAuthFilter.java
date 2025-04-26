package com.mycity.apigateway.config;
 
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.ServerWebExchange;
 
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
 
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
 
    private final CorsWebFilter corsWebFilter;
    private final JwtService jwtService;
 
    public JwtAuthFilter(JwtService jwtService, CorsWebFilter corsWebFilter) {
        this.jwtService = jwtService;
        this.corsWebFilter = corsWebFilter;
    }
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println(" Request path: " + path);
 
        // Allow public auth paths
        List<String> publicPaths = List.of("/auth/", "/client/", "/public/", "/tripplanner/","/category/");

        boolean isPublic = publicPaths.stream().anyMatch(path::startsWith);

        if (isPublic) {
            System.out.println("Public path - skipping token validation: " + path);
            return chain.filter(exchange);
        }

 
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
 
        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println(" No JWT token found in request headers.");
            return unauthorized(exchange, "Missing Authorization token in headers");
        }
 
        token = token.substring(7); // Extract token from "Bearer <token>"
 
        // ✅ Validate token
        if (!jwtService.validateToken(token)) {
            System.out.println(" Token is invalid");
            return unauthorized(exchange, "Invalid JWT token");
        }
 
        Claims claims = jwtService.extractAllClaims(token);
        String role = claims.get("role", String.class);
        Long userId = claims.get("userId", Long.class);
 
        System.out.println(" Token validated");
        System.out.println(" Extracted UserID: " + userId);
        System.out.println(" Extracted Role: " + role);
 
        if (role == null || userId == null) {
            System.out.println(" Token is missing required claims (role/userId)");
            return unauthorized(exchange, "Missing role or userId in token");
        }
 
        // ✅ Role-based access check
        if ("USER".equals(role) && !path.startsWith("/user/")) {
            System.out.println(" USER role not allowed to access: " + path);
            return unauthorized(exchange, "Access Denied: USER role not allowed");
        }
        if ("MERCHANT".equals(role) && !path.startsWith("/merchant/")) {
            System.out.println(" MERCHANT role not allowed to access: " + path);
            return unauthorized(exchange, "Access Denied: MERCHANT role not allowed");
        }
        if ("ADMIN".equals(role) && !path.startsWith("/admin/")) {
            System.out.println(" ADMIN role not allowed to access: " + path);
            return unauthorized(exchange, "Access Denied: ADMIN role not allowed");
        }
        final String finalToken = token;
        	
        
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Id", String.valueOf(userId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + finalToken)
                        .build())
                .build();
 
        System.out.println(" Token validated and user authorized. Continuing to service.");
        return chain.filter(mutatedExchange);
    }
 
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        System.out.println(" Unauthorized: " + message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String body = String.format("{\"error\": \"%s\"}", message);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory()
                        .wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
 
    @Override
    public int getOrder() {
        return -1; // High priority
    }
}