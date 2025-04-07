package com.mycity.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long weatherId;

    @Column(name = "place_id")
    private Long placeId; // ID of the place from the Place service

    private String bestTimeToVisit; // E.g., "Spring and Autumn", "November to February"

    private String bestSeason; // E.g., "Summer", "Winter", "Spring-Autumn"


}

