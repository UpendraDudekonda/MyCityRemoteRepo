package com.mycity.client.place;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.client.exception.MediaServiceUnavailableException;
import com.mycity.client.exception.PlaceNotFoundException;
import com.mycity.client.exception.ReviewServiceUnavailableException;
import com.mycity.client.exception.EventServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientAboutPlaceDetailController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String PLACE_GETTING_PATH = "/place/about/{placeName}";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/about/place/{placeName}")
    public Mono<ResponseEntity<Map<String, Object>>> getPlaceDetails(@PathVariable String placeName) {
        return webClientBuilder.build()
                .get()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + PLACE_GETTING_PATH, placeName)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})

                // 404 from place-service
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    Map<String, Object> errorBody = Map.of(
                            "error", "Not Found",
                            "status", HttpStatus.NOT_FOUND.value(),
                            "message", "Place not found: " + placeName
                    );
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody));
                })

                // Handle structured error response from place-service
                .onErrorResume(WebClientResponseException.class, ex -> {
                    try {
                        Map<String, Object> errorMap = objectMapper.readValue(
                                ex.getResponseBodyAsString(),
                                new TypeReference<>() {}
                        );

                        // Optionally map specific errors to custom client exceptions
                        String errorType = (String) errorMap.get("error");

                        if ("Review Service Unavailable".equalsIgnoreCase(errorType)) {
                            throw new ReviewServiceUnavailableException((String) errorMap.get("message"));
                        } else if ("Media Service Unavailable".equalsIgnoreCase(errorType)) {
                            throw new MediaServiceUnavailableException((String) errorMap.get("message"));
                        } else if ("Event Service Unavailable".equalsIgnoreCase(errorType)) {
                            throw new EventServiceUnavailableException((String) errorMap.get("message"));
                        }

                        // Forward error as-is
                        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(errorMap));

                    } catch (Exception parseEx) {
                        // Fallback in case of parse error
                        Map<String, Object> fallback = Map.of(
                                "error", "Failed to retrieve place details",
                                "status", ex.getStatusCode().value(),
                                "message", ex.getMessage()
                        );
                        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(fallback));
                    }
                })

                // Handle any other unexpected errors
                .onErrorResume(Exception.class, ex -> {
                    Map<String, Object> errorBody = Map.of(
                            "error", "Internal Server Error",
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", ex.getMessage()
                    );
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody));
                });
    }
}
