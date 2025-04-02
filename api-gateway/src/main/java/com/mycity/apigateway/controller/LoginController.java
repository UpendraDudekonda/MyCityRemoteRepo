package com.mycity.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.apigateway.dto.UserLoginRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class LoginController {
	
private final WebClient.Builder webClientBuilder;
	
	 private static final String USER_SERVICE_NAME = "USER-SERVICE"; // Using service name for discovery
	    private static final String USER_LOGIN_PATH = "/auth/user/login";

	    @PostMapping("/user")
	    public Mono<ResponseEntity<String>> loginUser(@Valid @RequestBody UserLoginRequest request) { // Renamed method to loginUser
	        return webClientBuilder.build()
	                .post()
	                .uri("lb://" + USER_SERVICE_NAME + USER_LOGIN_PATH) // Using service name and correct path
	                .body(Mono.just(request), UserLoginRequest.class)
	                .retrieve()
	                .toEntity(String.class)
	                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
	    }
	 
	 
}
