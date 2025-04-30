package com.mycity.location.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.mycity.shared.locationdto.GeoRequestDTO;
import com.mycity.shared.locationdto.LocationDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;

import reactor.core.publisher.Mono;

public interface GeoServiceInterface {

    // For geocoding both source and destination districts
    Mono<Map<String, CoordinateDTO>> getCoordinatesForSourceAndDestination(GeoRequestDTO requestDTO);

    // For geocoding a single district/city
    Mono<CoordinateDTO> getCoordinatesByCity(String city);

	Mono<ResponseEntity<? extends Object>> getCoordinatesByPlaceName(String placeName);

//	Mono<ResponseEntity<Map<String, CoordinateDTO>>> getLocationDetailsByPlaceName(String placeName);

}
