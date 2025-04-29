package com.mycity.trip.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Coordinate {
    @Id
    @GeneratedValue
    private Long id;

    private double latitude;
    private double longitude;

    @ManyToOne
    private Route route;
}


