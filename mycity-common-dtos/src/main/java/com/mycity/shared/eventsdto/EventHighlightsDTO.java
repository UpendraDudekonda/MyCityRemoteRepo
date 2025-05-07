package com.mycity.shared.eventsdto;


import java.time.LocalTime;

import lombok.Data;

@Data
	public class EventHighlightsDTO {
	  
	    private String date;
	    private LocalTime time;
	    private String activityName;
	    
	
}
