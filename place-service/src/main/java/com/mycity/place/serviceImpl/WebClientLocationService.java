package com.mycity.place.serviceImpl;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.locationdto.LocationDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;

import lombok.NonNull;

@Service
public class WebClientLocationService {
  
    @Autowired
    private WebClient.Builder webClientBuilder;

    // Define constants for location service URL and paths
    private static final String LOCATION_SERVICE = "LOCATION-SERVICE";  // The service name in the load balancer or registry
    private static final String LOCATION_FETCH_PATH = "/location/place-location/{placeName}";  // The endpoint to fetch location info for a specific place
    private static final String PLACE_CO_ORDINATES="/location/co-ordinates/{placeName}";
    // Method to fetch the location details for a given placeId from the LOCATION-SERVICE
    public CompletableFuture<LocationDTO> fetchLocationFromLocationService(@NonNull String placeName) {
        return webClientBuilder.baseUrl("lb://" + LOCATION_SERVICE)  // Load-balanced URI to call the LOCATION-SERVICE
                .build()
                .get()
                .uri(LOCATION_FETCH_PATH, placeName)  // Replace the {placeId} in the path with the actual placeId
                .retrieve()
                .bodyToMono(LocationDTO.class)  // Map the response body to a LocationDTO object
                .toFuture();  // Return the result as a CompletableFuture
    }

    public CompletableFuture<CoordinateDTO> fetchCoordinatesByPlaceName(String placeName) {
        return webClientBuilder.baseUrl("lb://" + LOCATION_SERVICE)  // Assuming load-balanced service URL
                .build()
                .post()
                .uri(PLACE_CO_ORDINATES, placeName)  // Replace with the actual API path
                .retrieve()
                .bodyToMono(CoordinateDTO.class)  // Expect the response to be of type CoordinateDTO
                .toFuture();  // Return the result as CompletableFuture
    }

}
