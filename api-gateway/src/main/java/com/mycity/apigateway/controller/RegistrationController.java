package com.mycity.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.merchantdto.MerchantRegRequest;
import com.mycity.shared.userdto.UserRegRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final WebClient.Builder webClientBuilder;

    private static final String USER_SERVICE_NAME = "user-service"; 
    private static final String USER_REGISTER_PATH = "/auth/user/start-registration";
    private static final String USER_COMPLETE_REGISTER_PATH = "/auth/user/complete-registration";
    private static final String MERCHANT_SERVICE_NAME = "merchant-service"; 
    private static final String MERCHANT_REGISTER_PATH = "/auth/merchant/register";

    @PostMapping("/user")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody UserRegRequest request) {
        System.out.println("Received User Registration Request: " + request);
        return webClientBuilder.build().post().uri("lb://" + USER_SERVICE_NAME + USER_REGISTER_PATH)
                .body(Mono.just(request), UserRegRequest.class).retrieve()
                .toEntity(String.class)
                .doOnTerminate(() -> System.out.println("Request completed"))
                .doOnError(error -> System.out.println("Error during request: " + error.getMessage()))
                .map(response -> {
                    System.out.println("Received Response: " + response.getBody());
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                });
    }

    @PostMapping("/user/complete")
    public Mono<ResponseEntity<String>> completeUserRegistration(@RequestBody UserRegRequest request,
                                                                  @RequestParam String otp) {
        System.out.println("Complete User Registration Request: OTP = " + otp + ", Request = " + request);
        return webClientBuilder.build().post()
                .uri("lb://" + USER_SERVICE_NAME + USER_COMPLETE_REGISTER_PATH + "?otp=" + otp)
                .body(Mono.just(request), UserRegRequest.class).retrieve()
                .toEntity(String.class)
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }

    @PostMapping("/merchant")
    public Mono<ResponseEntity<String>> registerMerchant(@RequestBody MerchantRegRequest request) {
        return webClientBuilder.build().post().uri("lb://" + MERCHANT_SERVICE_NAME + MERCHANT_REGISTER_PATH)
                .body(Mono.just(request), MerchantRegRequest.class).retrieve()
                .toEntity(String.class)
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }
}
