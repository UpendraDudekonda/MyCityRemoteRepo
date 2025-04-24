package com.mycity.client.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.placedto.PlaceDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/place")
@RequiredArgsConstructor
public class ClientPlaceController
{
	private final WebClient.Builder webClientBuilder;
	
	private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String PLACE_REGISTRATION_PATH = "/place/newplace/add";

    @PostMapping("/add")
    public Mono<String> addPlace(@RequestBody PlaceDTO dto) { // Removed @RequestHeader
        System.out.println("ClientPlaceController.addPlace()");
        return webClientBuilder.build()
                .post()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + PLACE_REGISTRATION_PATH)
                // Removed .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .body(Mono.just(dto), PlaceDTO.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Place: " + clientResponse.statusCode() + " - " + errorBody))))
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Failed to Add Place: " + e.getMessage()));
    }

}
