package com.mycity.trip.entity;

import java.util.List;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Route {
    private List<Coordinate> coordinates; // List of latitude/longitude points defining the route
    private String polyline;             // Encoded polyline string for map display (common in map APIs)
    private double distance;             // Total distance of the route (in meters or kilometers)
    private double duration;             // Estimated travel time (in seconds or minutes)
    // Optional:
    private List<Waypoint> waypoints;   // Significant points along the route
    private String startAddress;
    private String endAddress;
    // Getters and setters
}

