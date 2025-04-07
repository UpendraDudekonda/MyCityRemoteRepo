package com.mycity.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.merchant.dto.MerchantRegRequest;
import com.mycity.shared.user.dto.UserRegRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/auth/register")
@RequiredArgsConstructor
public class RegistrationController {
	
	
	private final WebClient.Builder webClientBuilder;
	
	 	private static final String USER_SERVICE_NAME = "USER-SERVICE"; // Using service name for discovery
	    private static final String USER_REGISTER_PATH = "/auth/user/register";
	    
	    
	    private static final String MERCHANT_SERVICE_NAME = "MERCHANT-SERVICE"; // Using service name for discovery
	    private static final String MERCHANT_REGISTER_PATH = "/auth/merchant/register";
	    
	 	@PostMapping("/user")
	    public Mono<ResponseEntity<String>> registerUser(@RequestBody UserRegRequest request) {
	        return webClientBuilder.build()
	                .post()
	                .uri("lb://" +USER_SERVICE_NAME + USER_REGISTER_PATH)
	                .body(Mono.just(request), UserRegRequest.class)
	                .retrieve()
	                .toEntity(String.class)
	                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
	    }
	 	
	 	@PostMapping("/merchant")
	    public Mono<ResponseEntity<String>> registerMerchant(@RequestBody MerchantRegRequest request) {
	        return webClientBuilder.build()
	                .post()
	                .uri("lb://" + MERCHANT_SERVICE_NAME + MERCHANT_REGISTER_PATH)
	                .body(Mono.just(request), MerchantRegRequest.class)
	                .retrieve()
	                .toEntity(String.class)
	                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
	    }
	 	
	 	
	 	
	 	
	 	
	
	
}
