package com.mycity.shared.eventsdto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
	public class EventHighlightsDTO {
	  
	    private LocalDate Date;
	    private LocalTime Time;
	    private String activityName;
	
}
