package com.mycity.tripplanner.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name= "TripPlanner")
public class TripPlanner {


	  	private Long tripId;
	    private String tripName;
	    private Long userId;
	    private String source;
	    private String destinations;
	    private List<LocalDate> tripDates;
	    private List<LocalTime> tripTimes;
	    private String cabDetails;


	
}
