package com.mycity.trip.entity;


import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TripPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id;
    private String externalApiId; // Reference to the external plan
    private String userId; // Link to your user (assuming users exist)
    private String source;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    // Store the itinerary structure - could be a JSON string, or mapped to other entities
    // Example: Using a String to store the processed JSON itinerary
    private String itineraryJson; // Store the processed TripResponseDto as JSON string
    private LocalDate createdDate;

    // Need a JPA converter if storing complex objects directly, or model as relations
    // @Convert(converter = TripResponseDtoConverter.class) // Example converter
    // private TripResponseDto itinerary;
}

