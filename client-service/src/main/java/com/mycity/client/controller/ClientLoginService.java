package com.mycity.client.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.shared.admindto.AdminLoginRequest;
import com.mycity.shared.merchantdto.MerchantLoginRequest;
import com.mycity.shared.userdto.UserLoginRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientLoginService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";

    @PostMapping("/login/user")
    public Mono<String> loginUser(@RequestBody UserLoginRequest request) {
        return forwardLogin(request, "/auth/login/user", UserLoginRequest.class);
    }

    @PostMapping("/login/merchant")
    public Mono<String> loginMerchant(@RequestBody MerchantLoginRequest request) {
        return forwardLogin(request, "/auth/login/merchant", MerchantLoginRequest.class);
    }

    @PostMapping("/login/admin")
    public Mono<String> loginAdmin(@RequestBody AdminLoginRequest request) {
        return forwardLogin(request, "/auth/login/admin", AdminLoginRequest.class);
    }

    private <T> Mono<String> forwardLogin(T request, String path, Class<T> typeclass) {
        return webClientBuilder.build()
            .post()
            .uri("lb://" + API_GATEWAY_SERVICE_NAME + path)
            .body(Mono.just(request), typeclass)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                res -> res.bodyToMono(String.class)
                    .flatMap(body -> {
                        try {
                            Map<String, Object> map = objectMapper.readValue(body, Map.class);
                            String message = (String) map.getOrDefault("message", "Unknown error");
                            return Mono.error(new RuntimeException(message));
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("Invalid error format"));
                        }
                    })
            )
            .bodyToMono(String.class)
            .onErrorResume(e -> Mono.just("{\"error\":\"" + e.getMessage() + "\"}"));
    }
}
