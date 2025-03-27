package com.mycity.navigation.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Navigation {

	
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
