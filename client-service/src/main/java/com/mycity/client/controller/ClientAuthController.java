package com.mycity.client.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.emaildto.VerifyOtpDTO;
import com.mycity.shared.merchantdto.MerchantRegRequest;
import com.mycity.shared.userdto.UserRegRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientAuthController {

    private final WebClient.Builder webClientBuilder;

    private static final String API_GATEWAY_URL = "lb://API-GATEWAY";

    // Step 1: Start Registration (Send OTP)
    @PostMapping("/auth/startreg")
    public Mono<String> startRegistration(@RequestBody RequestOtpDTO request) {
        return forwardRequest(
            request,
            "/auth/email/send",
            RequestOtpDTO.class
        );
    }
    @PostMapping("/auth/verifyotp")
    public Mono<String> verifyOtpMono(@RequestBody VerifyOtpDTO verifyOtpDTO) {
        return webClientBuilder.build()
            .post()
            .uri(API_GATEWAY_URL + "/auth/otp/verifyotp")
            .bodyValue(verifyOtpDTO)
            .retrieve()
            .onStatus(HttpStatusCode::isError, res ->
                res.bodyToMono(String.class)
                   .flatMap(body -> Mono.error(new RuntimeException("OTP verification failed: " + body)))
            )
            .bodyToMono(String.class)
            .onErrorResume(e -> Mono.just("{\"error\":\"" + e.getMessage() + "\"}"));
    }



    @PostMapping("/auth/completereg/user")
    public Mono<String> completeUserRegistration(@RequestBody UserRegRequest request) {

        // Make sure request.otpVerified is true
        if (!Boolean.TRUE.equals(request.isOtpVerified())) {
            return Mono.just("{\"error\":\"OTP not verified. Please verify OTP first.\"}");
        }

        return webClientBuilder.build()
            .post()
            .uri(API_GATEWAY_URL + "/auth/register/user")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, res ->
                res.bodyToMono(String.class)
                   .flatMap(body -> Mono.error(new RuntimeException("User Registration failed: " + body)))
            )
            .bodyToMono(String.class)
            .onErrorResume(e -> Mono.just("{\"error\":\"" + e.getMessage() + "\"}"));
    }
    
    @PostMapping("/auth/completereg/merchant")
    public Mono<String> completeMerchantRegistration(@RequestBody MerchantRegRequest request) {

        // Make sure request.otpVerified is true
        if (!Boolean.TRUE.equals(request.isOtpVerified())) {
            return Mono.just("{\"error\":\"OTP not verified. Please verify OTP first.\"}");
        }

        return webClientBuilder.build()
            .post()
            .uri(API_GATEWAY_URL + "/auth/register/merchant")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, res ->
                res.bodyToMono(String.class)
                   .flatMap(body -> Mono.error(new RuntimeException("Merchant Registration failed: " + body)))
            )
            .bodyToMono(String.class)
            .onErrorResume(e -> Mono.just("{\"error\":\"" + e.getMessage() + "\"}"));
    }
    
    // Common POST request forwarding
    private <T> Mono<String> forwardRequest(T request, String path, Class<T> typeClass) {
        return webClientBuilder.build()
            .post()
            .uri(API_GATEWAY_URL + path)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, res ->
                res.bodyToMono(String.class)
                   .flatMap(body -> Mono.error(new RuntimeException(body)))
            )
            .bodyToMono(String.class)
            .onErrorResume(e -> Mono.just("{\"error\":\"" + e.getMessage() + "\"}"));
    }


}
