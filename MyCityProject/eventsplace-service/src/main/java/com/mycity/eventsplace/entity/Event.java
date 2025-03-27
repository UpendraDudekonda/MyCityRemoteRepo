package com.mycity.eventsplace.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="table")
public class Event {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String eventName;
	    private String description;
	    private String location;
	    private LocalDate eventDate;
	    private LocalTime eventTime;

//	    @ManyToOne
//	    @JoinColumn(name = "place_id")
//	    private Place place;  // Linking the event to a place

	    private String organizer;
	    private String contactInfo;
	    
	    private LocalDateTime lastUpdated;

	    public Event() {}


}
