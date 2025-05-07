package com.mycity.eventsplace.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventId;

	private String eventName;
     
	 private String city;
	 
	@Column(columnDefinition = "TEXT")
	private String description;

	private LocalDate date;

	private LocalTime duration;
    

 
	@ElementCollection
	@CollectionTable(name = "event_place" ,joinColumns=@JoinColumn(name="event_id"))

	private List<String> eventPlaces;
	
	
	@ElementCollection
	@CollectionTable(name = "event_schedule", joinColumns = @JoinColumn(name = "event_id"))
	private List<EventHighlights> schedule;

	


}
