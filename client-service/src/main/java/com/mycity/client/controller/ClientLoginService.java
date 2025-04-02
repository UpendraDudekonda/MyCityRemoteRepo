package com.mycity.client.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.client.dto.UserLoginRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/login")
@RequiredArgsConstructor
public class ClientLoginService {
	
	private final WebClient.Builder webClientBuilder;
	
	private static final String API_GATEWAY_SERVICE_NAME="API-GATEWAY";
	
	private static final String USER_LOGIN_PATH="/auth/login/user";
	
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
	
	
	

	
}
