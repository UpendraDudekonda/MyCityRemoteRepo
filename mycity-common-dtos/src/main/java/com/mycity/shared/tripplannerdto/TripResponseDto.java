package com.mycity.shared.tripplannerdto;



import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripResponseDto {
    private String planId; // Your internal ID, or the external one
    private String tripSummary;
    private List<DayPlanDto> dailyPlans; // Your representation of daily plans
}

