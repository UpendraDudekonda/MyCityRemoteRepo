package com.mycity.shared.tripplannerdto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {
    private List<CoordinateDTO> coordinates;
    private String polyline;
    private double distance;
    private double duration;
    private List<WaypointDTO> waypoints;
    private String startAddress;
    private String endAddress;
    
}