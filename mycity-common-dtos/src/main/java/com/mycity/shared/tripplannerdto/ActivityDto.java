package com.mycity.shared.tripplannerdto;


import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDto {
    private String name; // POI/Location name
    private String location; // Address or coordinates string
    private LocalTime startTime; // Start time for the activity
    private LocalTime endTime; // End time (optional)
    private String description;
    private String type; // e.g., "attraction", "restaurant", "travel"
    // ... other details provided by the external API (e.g., lat/lon, duration, etc.)
}

