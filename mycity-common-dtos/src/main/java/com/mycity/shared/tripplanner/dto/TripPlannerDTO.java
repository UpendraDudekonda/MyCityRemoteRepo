package com.mycity.shared.tripplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPlannerDTO {

    private Long tripId;
    private String tripName;
    private Long userId;
    private String source;
    private String destinations;
    private String cabDetails;
}


