package com.mycity.trip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.tripplannerdto.TripPlanResponseDTO;
import com.mycity.shared.tripplannerdto.TripRequestDTO;
import com.mycity.trip.service.TripPlannerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tripplanner")
@RequiredArgsConstructor
@Slf4j
public class TripPlannerController {

    private final TripPlannerService tripPlannerService;

    @PostMapping("/public/trip-plan")
    public Mono<ResponseEntity<TripPlanResponseDTO>> getTripPlan(@RequestBody TripRequestDTO tripRequestDTO) {
        log.info("📩 Received trip planning request from '{}' to '{}'", tripRequestDTO.getSource(), tripRequestDTO.getDestination());

        return tripPlannerService.generateTripPlan(tripRequestDTO)
                .doOnNext(response -> log.info("✅ Trip plan generated successfully for '{}' -> '{}'", tripRequestDTO.getSource(), tripRequestDTO.getDestination()))
                .onErrorResume(ex -> {
                    log.error("❌ Failed to generate trip plan: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.status(500).build());
                });
    }
}