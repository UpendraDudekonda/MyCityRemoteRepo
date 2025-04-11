package com.mycity.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.admindto.AdminLoginRequest;
import com.mycity.shared.merchantdto.MerchantLoginReq;
import com.mycity.shared.userdto.UserLoginRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class LoginController {

    private final WebClient.Builder webClientBuilder;

    private static final String USER_SERVICE_NAME = "user-service"; // Using service name for discovery
    
    private static final String MERCHANT_SERVICE_NAME = "merchant-service";
    
    private static final String ADMIN_SERVICE_NAME = "admin-service";
    
    private static final String USER_LOGIN_PATH = "/auth/user/login";
    
    private static final String MERCHANT_LOGIN_PATH = "/auth/merchant/login";
    
    private static final String ADMIN_LOGIN_PATH = "/auth/admin/login";

    @PostMapping("/user")
    public Mono<ResponseEntity<String>> LoginUser(@RequestBody UserLoginRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("lb://" +USER_SERVICE_NAME + USER_LOGIN_PATH)
                .body(Mono.just(request), UserLoginRequest.class)
                .retrieve()
                .toEntity(String.class)
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }
    
    @PostMapping("/merchant")
    public Mono<ResponseEntity<String>> LoginMercahnt(@RequestBody MerchantLoginReq request) {
        return webClientBuilder.build()
                .post()
                .uri("lb://" +MERCHANT_SERVICE_NAME + MERCHANT_LOGIN_PATH)
                .body(Mono.just(request), MerchantLoginReq.class)
                .retrieve()
                .toEntity(String.class)
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }
    
    @PostMapping("/admin")
    public Mono<ResponseEntity<String>> LoginAdmin(@RequestBody AdminLoginRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("lb://" +ADMIN_SERVICE_NAME + ADMIN_LOGIN_PATH)
                .body(Mono.just(request), AdminLoginRequest.class)
                .retrieve()
                .toEntity(String.class)
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }
}