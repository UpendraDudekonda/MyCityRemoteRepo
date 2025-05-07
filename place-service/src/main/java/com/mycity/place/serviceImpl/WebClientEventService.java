package com.mycity.place.serviceImpl;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import com.mycity.shared.placedto.AboutPlaceEventDTO;

@Service
public class WebClientEventService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String EVENT_SERVICE = "EVENT-SERVICE"; // service name in registry/load balancer
    private static final String EVENT_FETCH_PATH = "/events/place/{placeId}"; // endpoint to fetch events

    public CompletableFuture<List<AboutPlaceEventDTO>> fetchEvents(Long placeId) {
        return webClientBuilder.baseUrl("lb://" + EVENT_SERVICE)
                .build()
                .get()
                .uri(EVENT_FETCH_PATH, placeId)
                .retrieve()
                .bodyToFlux(AboutPlaceEventDTO.class)
                .collectList()
                .onErrorResume(e -> {
                    handleError(e);
                    return reactor.core.publisher.Mono.just(List.of()); // Return a Mono here
                })
                .toFuture(); // Then convert to CompletableFuture
    }

    private void handleError(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) e;
            System.err.println("Error from EVENT-SERVICE: " + ex.getRawStatusCode() + " - " + ex.getResponseBodyAsString());
        } else {
            System.err.println("Unexpected error fetching events: " + e.getMessage());
        }
    }
}

