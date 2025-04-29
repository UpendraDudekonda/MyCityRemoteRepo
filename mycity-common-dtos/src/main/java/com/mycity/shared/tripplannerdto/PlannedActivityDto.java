package com.mycity.shared.tripplannerdto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlannedActivityDto {
    private String name;
    private String location; // Could be address string or coordinates string
    private LocalTime startTime;
    private LocalTime endTime; // Optional
    private String description;
    private String category; // Your internal category mapping
    // Add fields needed for map display (e.g., double latitude, double longitude)
    private Double latitude;
    private Double longitude;
}

