package com.mycity.client.place;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientAboutPlaceDetailController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Use Eureka service name for API Gateway
    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String PLACE_GETTING_PATH = "/place/about/{placeId}";

    @GetMapping("/about/place/{placeId}")
    public Mono<ResponseEntity<Map<String, Object>>> getPlaceDetails(@PathVariable Long placeId) {
        return webClientBuilder.build()
                .get()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + PLACE_GETTING_PATH, placeId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnTerminate(() -> System.out.println("Completed request"))
                .doOnError(error -> System.err.println("Error occurred: " + error.getMessage()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(404).body(Map.of("error", "Place not found")));
    }
}
