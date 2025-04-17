package com.mycity.shared.tripplannerdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPlannerDTO {

    private Long tripId;
    private String tripName;    
    private String source;
    private String destinations;

}


