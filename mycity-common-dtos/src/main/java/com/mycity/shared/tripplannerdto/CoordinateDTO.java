package com.mycity.shared.tripplannerdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoordinateDTO {
    private double latitude;
    private double longitude;
    // Getters and setters
}