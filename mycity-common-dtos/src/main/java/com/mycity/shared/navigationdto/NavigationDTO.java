package com.mycity.shared.navigationdto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class NavigationDTO {

    private Long navigationId;
    private Long startLocationId;
    private Long endLocationId;
    private List<Long> waypointIds;
    private String transportationMode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String instructions;
    private List<String> steps;
    private Double distance;
    private Double duration;
  
    private String trafficInformation;
    
   


}