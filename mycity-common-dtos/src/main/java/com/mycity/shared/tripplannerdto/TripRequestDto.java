package com.mycity.shared.tripplannerdto;
 
import java.time.LocalDate;
 
import lombok.AllArgsConstructor;

 
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripRequestDto {
 
	  
	    private String source; // e.g., "New York, NY"
	    private String destination; // e.g., "Los Angeles, CA"
	    private LocalDate startDate;
 
	    private LocalDate endDate;
	    // Add other parameters like interests, travel mode, etc.
	    private String travelMode; // e.g., "driving", "flying"
	    private String interests; // e.g., "museums, food"

 
}