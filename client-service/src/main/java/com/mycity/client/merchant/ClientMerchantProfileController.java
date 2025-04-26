package com.mycity.client.merchant;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.client.config.CookieTokenExtractor;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientMerchantProfileController {

    private final WebClient.Builder webClientBuilder;
    private final CookieTokenExtractor cookieTokenExtractor;

    private static final Logger logger = LoggerFactory.getLogger(ClientMerchantProfileController.class);

    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String MERCHANT_PROFILE_PATH_ON_GATEWAY = "/merchant/profile"; // Updated path for merchant profile

    @GetMapping("/profile/merchant")
    public Mono<ResponseEntity<String>> getMerchantProfile(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie) {

        // Extract token using the injected CookieTokenExtractor
        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);

        if (token == null || token.isEmpty()) {
            logger.error("❌ Missing Authorization token from cookie");
            return Mono.just(ResponseEntity.status(400).body("Authorization token is missing"));
        }

        logger.info("📩 Forwarding Authorization token to API Gateway: {}", token);
        System.out.println(token);

        return webClientBuilder.build()
                .get()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + MERCHANT_PROFILE_PATH_ON_GATEWAY) // Updated URI for merchant profile
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)  // Forward token to API Gateway
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    logger.info("✅ Received response from API Gateway with status: {}", response.getStatusCode());
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                })
                .onErrorResume(ex -> {
                    logger.error("❌ Error forwarding to API Gateway: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Something went wrong...."));
                });
    }
}