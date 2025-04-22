package com.mycity.location.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
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

}