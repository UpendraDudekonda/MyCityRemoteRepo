package com.mycity.trip.service;

import org.springframework.http.ResponseEntity;

import com.mycity.shared.tripplannerdto.TripPlanResponseDTO;
import com.mycity.shared.tripplannerdto.TripRequestDTO;

import reactor.core.publisher.Mono;

public interface TripPlannerService {

	Mono<ResponseEntity<TripPlanResponseDTO>> generateTripPlan(TripRequestDTO tripRequestDTO);

}
