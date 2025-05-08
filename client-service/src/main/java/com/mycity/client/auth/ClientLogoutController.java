package com.mycity.client.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
public class ClientLogoutController {
	
	 private static final String APIGATEWAY_SERVICE_NAME = "API-GATEWAY"; // Use the actual service ID registered in discovery
	 private static final String LOGOUT_PATH = "/auth/logout";
	 
	// String uri = "lb://" + APIGATEWAY_SERVICE_NAME + INITIATE_PATH;

	 @Autowired
	    private WebClient.Builder webClientBuilder;

	    @PostMapping("/logout")
	    public Mono<ResponseEntity<String>> logout() {
	        // Forward the request to the API Gateway's /logout endpoint
	        return webClientBuilder.build()
	                .post()
	                .uri("lb://" + APIGATEWAY_SERVICE_NAME + LOGOUT_PATH) // API Gateway URL
	                .retrieve()
	                .toEntity(String.class) // Get the response from the API Gateway
	                .doOnSuccess(entity -> {
	                    // Optionally log or handle the response if needed
	                });
	    }
	
}
