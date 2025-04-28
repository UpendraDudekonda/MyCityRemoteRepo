package com.mycity.shared.tripplannerdto;


import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayItineraryDto {
	
    private LocalDate date;
    private List<ActivityDto> activities; // Assuming a list of activities/stops per day
}
