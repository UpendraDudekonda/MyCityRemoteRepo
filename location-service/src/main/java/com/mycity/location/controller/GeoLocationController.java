package com.mycity.location.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.location.service.GeoServiceInterface;
import com.mycity.shared.locationdto.GeoRequestDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
@Slf4j
public class GeoLocationController {

    private final GeoServiceInterface geoService;
    
    
//    @GetMapping("/coordinates")
//    public Mono<CoordinateDTO> getCoordinatesByCity(@RequestParam String city) {
//        return geoService.getCoordinatesByCity(city);
//    }
    

    @PostMapping("/geocode")
    public Mono<ResponseEntity<Map<String, CoordinateDTO>>> geocodeDistricts(@RequestBody GeoRequestDTO request) {
        return geoService.getCoordinatesForSourceAndDestination(request)
                .map(coords -> {
                    if (coords.isEmpty() || coords.get("source") == null || coords.get("destination") == null) {
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(coords);
                });    
     
    }
    
 // Endpoint to fetch coordinates by place name using PathVariable
    @PostMapping("/co-ordinates/{placeName}")
    public Mono<ResponseEntity<? extends Object>> getCoordinatesByPlaceName(@PathVariable String placeName) {
        // Log the incoming request
    	
        log.info("Fetching coordinates for place: {}", placeName);

        // Call the service method to fetch coordinates based on the place name
        return geoService.getCoordinatesByPlaceName(placeName)
                .flatMap(response -> {
                    // Log the response status code for debugging purposes
                    log.info("Received response with status code: {}", response.getStatusCode());

                    // If the response indicates an error (4xx or 5xx), return that response as is
                    if (response.getStatusCode().isError()) {
                        log.error("Error response received: {}", response.getStatusCode());
                        return Mono.just(ResponseEntity.status(response.getStatusCode()).build());
                    }

                    // Extract the body (Map<String, CoordinateDTO>) from the response
                    Object body = response.getBody();
                    if (body == null || !((Map<String, CoordinateDTO>) body).containsKey("location")) {
                        log.warn("No coordinates found for place: {}", placeName);
                        return Mono.just(ResponseEntity.notFound().build()); // Return 404 if no location is found
                    }

                    // If coordinates are found, log them and return the response
                    CoordinateDTO coordinateDTO = ((Map<String, CoordinateDTO>) body).get("location");
                    log.info("Coordinates found for place: {} - Latitude: {}, Longitude: {}",
                            placeName, coordinateDTO.getLatitude(), coordinateDTO.getLongitude());
                    return Mono.just(ResponseEntity.ok(coordinateDTO)); // Return 200 OK with coordinates
                })
                .doOnError(e -> log.error("Error occurred while fetching coordinates for place: {}", placeName, e)); // Log error if something goes wrong
    }
  



}