package com.mycity.shared.tripplannerdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripRequestDTO {
    private String source;
    private String destination;
    // Optional parameters
    private CoordinateDTO sourceCoordinates;
    private CoordinateDTO destinationCoordinates;
    
}