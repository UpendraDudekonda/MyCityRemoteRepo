package com.mycity.apigateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.emaildto.VerifyOtpDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/verify-otp")
public class ApiGateWayVerifyOtpController {
	
	@Autowired
    private WebClient.Builder webClientBuilder;

    private static final String EMAIL_SERVICE_URL = "http://localhost:8085";  // Email service URL and port
    
    private static final String OTP_VERIFY_PATH_USER = "/auth/user/verify-otp";  
    
    private static final String OTP_VERIFY_PATH_MERCHANT = "/auth/merchant/verify-otp";    

	 // Endpoint to verify OTP
    @PostMapping("/user")
    public Mono<ResponseEntity<String>> userVerifyOTP(@RequestBody VerifyOtpDTO request) {
        return webClientBuilder.build()
                .post()
                .uri(EMAIL_SERVICE_URL + OTP_VERIFY_PATH_USER) // Verify OTP (using the correct email service URL)
                .bodyValue(request) // Send email and OTP in the request body
                .retrieve()
                .bodyToMono(Boolean.class)
                .map(isVerified -> {
                    if (isVerified) {
                        return ResponseEntity.ok("OTP verified successfully.");
                    } else {
                        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
                    }
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Error verifying OTP: " + e.getMessage())));
    }
    
    @PostMapping("/merchant")
    public Mono<ResponseEntity<String>> merchantVerifyOTP(@RequestBody VerifyOtpDTO request) {
        return webClientBuilder.build()
                .post()
                .uri(EMAIL_SERVICE_URL + OTP_VERIFY_PATH_MERCHANT) // Verify OTP (using the correct email service URL)
                .bodyValue(request) // Send email and OTP in the request body
                .retrieve()
                .bodyToMono(Boolean.class)
                .map(isVerified -> {
                    if (isVerified) {
                        return ResponseEntity.ok("OTP verified successfully.");
                    } else {
                        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
                    }
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Error verifying OTP: " + e.getMessage())));
    }
}
