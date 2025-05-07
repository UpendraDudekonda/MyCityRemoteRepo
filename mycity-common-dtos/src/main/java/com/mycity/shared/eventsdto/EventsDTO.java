package com.mycity.shared.eventsdto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsDTO {
     	
	
    private String eventName;
    private LocalTime duration;
    private String description;
   
    private LocalTime time;
    private String city;
    private String date;

    private List<String> eventPlaces;
    private List<EventHighlightsDTO> schedule;
	
}

