package com.mycity.shared.tripplannerdto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalApiResponseDto {
	
    private String tripId; // External API's ID for the plan
    private String summary;
    private List<DayItineraryDto> itinerary; // Assuming a list of days
    // ... other fields from external API response
}

