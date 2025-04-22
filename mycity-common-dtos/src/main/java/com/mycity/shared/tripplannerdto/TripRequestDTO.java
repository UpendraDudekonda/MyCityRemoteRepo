package com.mycity.shared.tripplannerdto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripRequestDTO {
    private String source;
    private String destination;
    // Optional parameters
 
 // New fields for date-based planning
    private LocalDate startDate;
    private LocalDate endDate;
    
    private int numberOfDays; // Optional – can be computed from dates
}