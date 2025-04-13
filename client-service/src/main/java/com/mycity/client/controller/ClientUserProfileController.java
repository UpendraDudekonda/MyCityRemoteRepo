package com.mycity.client.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientUserProfileController {

    private final WebClient.Builder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(ClientUserProfileController.class);
    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String USER_PROFILE_PATH_ON_GATEWAY = "/user/account/profile";

    @GetMapping("/profile/user")
    public Mono<ResponseEntity<String>> getUserProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        logger.info("Forwarding JWT token to user-service via gateway");

        return webClientBuilder.build()
                .get()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + USER_PROFILE_PATH_ON_GATEWAY)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    logger.info("Received status: {}", response.getStatusCode());
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                });
    }
}
