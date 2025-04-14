package com.mycity.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.placedto.PlaceDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("auth/place")
@RequiredArgsConstructor
public class PlaceController 
{ 
	private final WebClient.Builder webClientBuilder;
	
 	private static final String PLACE_SERVICE_NAME = "place-service"; // Using service name for discovery
    private static final String PLACE_REGISTER_PATH = "/auth/place-api/addplace";
    
    
 	@PostMapping("/register")
    public Mono<ResponseEntity<String>> registerPlace(@RequestBody PlaceDTO dto) {
 		System.out.println("PlaceController.registerPlace()");
        return webClientBuilder.build()
                .post()
                .uri("lb://" +PLACE_SERVICE_NAME + PLACE_REGISTER_PATH)
                .body(Mono.just(dto), PlaceDTO.class)
                .retrieve()
                .toEntity(String.class)
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }
}
