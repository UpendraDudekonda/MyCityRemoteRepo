package com.mycity.shared.eventsdto;


import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
	public class EventHighlightsDTO {
	  
	    private String date;
	    private LocalTime time;
	    private String activityName;
	    
	
}
