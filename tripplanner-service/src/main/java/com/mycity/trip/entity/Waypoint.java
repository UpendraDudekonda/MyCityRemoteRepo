package com.mycity.trip.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Waypoint {
    private Coordinate coordinate;
    private String address;
    // Optional:
    private String name;
    // Getters and setters
}