package com.mycity.client.auth;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public Mono<ResponseEntity<String>> loginUser(@RequestBody UserLoginRequest request) {
        return forwardLogin(request, "/auth/login/user", UserLoginRequest.class);
    }

    @PostMapping("/login/merchant")
    public Mono<ResponseEntity<String>> loginMerchant(@RequestBody MerchantLoginRequest request) {
        return forwardLogin(request, "/auth/login/merchant", MerchantLoginRequest.class);
    }

    @PostMapping("/login/admin")
    public Mono<ResponseEntity<String>> loginAdmin(@RequestBody AdminLoginRequest request) {
        return forwardLogin(request, "/auth/login/admin", AdminLoginRequest.class);
    }

    private <T> Mono<ResponseEntity<String>> forwardLogin(T request, String path, Class<T> typeclass) {
        return webClientBuilder.build()
            .post()
            .uri("lb://" + API_GATEWAY_SERVICE_NAME + path)
            .bodyValue(request)
            .exchangeToMono(response -> {
                Mono<String> bodyMono = response.bodyToMono(String.class);
                List<String> cookieHeaders = response.headers().header(HttpHeaders.SET_COOKIE);

                return bodyMono.map(body -> {
                    ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.statusCode());

                    // Forward all Set-Cookie headers to the client
                    for (String cookie : cookieHeaders) {
                        builder.header(HttpHeaders.SET_COOKIE, cookie);
                    }

                    return builder.body(body);
                });
            })
            .onErrorResume(e -> {
                String errorJson = "{\"error\":\"" + e.getMessage() + "\"}";
                return Mono.just(ResponseEntity.badRequest().body(errorJson));
            });
    }
}
