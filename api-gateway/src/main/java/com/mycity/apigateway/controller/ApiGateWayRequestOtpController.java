package com.mycity.apigateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.emaildto.VerifyOtpDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/request-otp")
public class ApiGateWayRequestOtpController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String EMAIL_SERVICE_NAME = "EMAIL-SERVICE";  // Email service URL and port
    
    private static final String OTP_REQUEST_PATH_USER = "/auth/user/generate-otp";  // Path for generating OTP
    
    private static final String OTP_REQUEST_PATH_MERCHANT = "/auth/merchant/generate-otp";

    // Endpoint to request OTP
    @PostMapping("/user")
    public Mono<ResponseEntity<String>> userRequestOTP(@RequestBody RequestOtpDTO request) {
        return webClientBuilder.build()
                .post()
                .uri("lb://" +EMAIL_SERVICE_NAME + OTP_REQUEST_PATH_USER) // Generate OTP (using the correct email service URL)
                .bodyValue(request) // Pass the request body (email)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> ResponseEntity.ok("OTP request initiated. Please check your email."))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Error initiating OTP request: " + e.getMessage())));
    }

    @PostMapping("/merchant")
    public Mono<ResponseEntity<String>> merchantRequestOTP(@RequestBody RequestOtpDTO request) {
        return webClientBuilder.build()
                .post()
                .uri("lb://" +EMAIL_SERVICE_NAME + OTP_REQUEST_PATH_MERCHANT)  // Generate OTP (using the correct email service URL)
                .bodyValue(request) // Pass the request body (email)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> ResponseEntity.ok("OTP request initiated. Please check your email."))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Error initiating OTP request: " + e.getMessage())));
    }
    
}
