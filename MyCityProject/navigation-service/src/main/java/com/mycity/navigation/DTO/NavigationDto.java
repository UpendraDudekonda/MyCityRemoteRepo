package com.mycity.navigation.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class NavigationDto {
	
	
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
