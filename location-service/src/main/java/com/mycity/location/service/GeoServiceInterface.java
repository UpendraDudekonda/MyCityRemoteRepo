package com.mycity.location.service;

import java.util.Map;

import com.mycity.shared.locationdto.GeoRequestDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;

import reactor.core.publisher.Mono;

public interface GeoServiceInterface {

    // For geocoding both source and destination districts
    Mono<Map<String, CoordinateDTO>> getCoordinatesForSourceAndDestination(GeoRequestDTO requestDTO);

    // For geocoding a single district/city
    Mono<CoordinateDTO> getCoordinatesByCity(String city);
}
