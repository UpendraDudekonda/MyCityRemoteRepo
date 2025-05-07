package com.mycity.client.tripplanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;


import com.mycity.shared.tripplannerdto.TripRequestDto;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientTripRequestController {

	private final WebClient.Builder webClientBuilder;
	
	private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
	
	private static final Logger logger = LoggerFactory.getLogger(ClientTripRequestController.class);
	
	
	@PostMapping("/public/trip-plan")
	public Mono<ResponseEntity<String>> getTripPlan(@RequestBody TripRequestDto tripRequest) {

	    logger.info("📩 Sending public trip plan request to API Gateway with source: {} and destination: {}",
	            tripRequest.getSource(), tripRequest.getDestination());

	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" + API_GATEWAY_SERVICE_NAME + "/tripplanner/trip-plan")
	            .bodyValue(tripRequest)
	            .retrieve()
	            .toEntity(String.class)
	            .map(response -> {
	                logger.info("✅ Received trip plan response with status: {}", response.getStatusCode());
	                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
	            })
	            .onErrorResume(ex -> {
	                logger.error("❌ Failed to fetch trip plan: {}", ex.getMessage());
	                return Mono.just(ResponseEntity.status(500).body("Something went wrong while fetching the trip plan."));
	            });
	}


	
	
}
