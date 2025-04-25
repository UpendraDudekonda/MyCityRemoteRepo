package com.mycity.shared.tripplannerdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaypointDTO {
	
    private CoordinateDTO coordinate;
    private String address;
    private String name;
    
}