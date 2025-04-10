package com.mycity.client.controller;

import org.springframework.http.HttpStatusCode;


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
@RequestMapping("/client/login")
@RequiredArgsConstructor
public class ClientLoginService {
	
	private final WebClient.Builder webClientBuilder;
	
	private static final String API_GATEWAY_SERVICE_NAME="API-GATEWAY";
	
	private static final String USER_LOGIN_PATH="/auth/login/user";
	
	private static final String MERCHANT_LOGIN_PATH="/auth/login/merchant";
	
	private static final String ADMIN_LOGIN_PATH="/auth/login/admin";
	
	@PostMapping("/user")
	public Mono<String> LoginUser(@RequestBody UserLoginRequest request) {
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + USER_LOGIN_PATH)
	            .body(Mono.just(request), UserLoginRequest.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Login failed: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Login failed: " + e.getMessage()));
	    
	}
	
	@PostMapping("/merchant")
	public Mono<String> LoginMerchant(@RequestBody MerchantLoginReq request) {
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + MERCHANT_LOGIN_PATH)
	            .body(Mono.just(request), MerchantLoginReq.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Login failed: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Login failed: " + e.getMessage()));
	    
	}
	
	@PostMapping("/admin")
	public Mono<String> LoginAdmin(@RequestBody AdminLoginRequest request) {
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + ADMIN_LOGIN_PATH)
	            .body(Mono.just(request), AdminLoginRequest.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Login failed: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Login failed: " + e.getMessage()));
	    
	}
}
