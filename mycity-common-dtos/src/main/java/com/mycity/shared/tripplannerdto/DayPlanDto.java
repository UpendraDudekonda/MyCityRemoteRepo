package com.mycity.shared.tripplannerdto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DayPlanDto {
	private LocalDate date;
	private String summary; // Optional summary for the day
	private List<PlannedActivityDto> activities;
}

