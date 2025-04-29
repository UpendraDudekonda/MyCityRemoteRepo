package com.mycity.trip.service;

import com.mycity.shared.tripplannerdto.TripRequestDto;
import com.mycity.shared.tripplannerdto.TripResponseDto;

import reactor.core.publisher.Mono;

public interface TripPlannerService {

	Mono<TripResponseDto> planTrip(TripRequestDto requestDto);

	

}
