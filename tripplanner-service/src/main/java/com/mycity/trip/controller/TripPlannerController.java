package com.mycity.trip.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.tripplannerdto.TripRequestDto;
import com.mycity.shared.tripplannerdto.TripResponseDto;
import com.mycity.trip.service.TripPlannerService; // Import the service interface

import reactor.core.publisher.Mono; // Assuming you have validation

@RestController
@RequestMapping("/tripplanner/trip-plan")
public class TripPlannerController {

    // Use Field Injection here for the service dependency
    @Autowired
    private TripPlannerService tripPlannerService;

    // No explicit constructor is needed here either if using field injection

    @PostMapping
    public Mono<ResponseEntity<TripResponseDto>> planTrip(@RequestBody TripRequestDto requestDto) {
    			
    		System.err.println("......entered into trip planner.....");
    			
    	return tripPlannerService.planTrip(requestDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                // Error handling can be added here or global handler
                ;
    }
}