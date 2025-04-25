package com.mycity.shared.eventsdto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.mycity.shared.placedto.PlaceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsDTO {
     	
    private String name;
    private LocalTime duration;
    private String description;
   
    private String city;
    private LocalDate date;

  
    private List<String> eventPlaces;
    private List<EventHighlightsDTO> schedule;
	
}

