package com.mycity.client.controller;

import org.springframework.http.HttpStatusCode;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.merchantdto.MerchantRegRequest;
import com.mycity.shared.userdto.UserRegRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/register")
@RequiredArgsConstructor
public class ClientRegistrationService {

	private final WebClient.Builder webClientBuilder;
	
	private static final String API_GATEWAY_SERVICE_NAME="API-GATEWAY";
	
	private static final String USER_REGISTRATION_PATH="/auth/register/user";
	
	private static final String MERCHANT_REGISTRATION_PATH="/auth/register/merchant";
	
	@PostMapping("/user")
	public Mono<String> registerUser(@RequestBody UserRegRequest request) {
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + USER_REGISTRATION_PATH)
	            .body(Mono.just(request), UserRegRequest.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Registration failed: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Registration failed: " + e.getMessage()));
	}
	
	@PostMapping("/merchant")
	public Mono<String> registerMerchant(@RequestBody MerchantRegRequest request) {
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" + API_GATEWAY_SERVICE_NAME + MERCHANT_REGISTRATION_PATH)
	            .body(Mono.just(request), MerchantRegRequest.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Registration failed: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Registration failed: " + e.getMessage()));
	}
	
	
}
