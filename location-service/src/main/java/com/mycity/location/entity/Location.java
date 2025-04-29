package com.mycity.location.entity;

import jakarta.persistence.Id;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;
    
    // Geographical coordinates
    private Double latitude;
    private Double longitude;

   
//    private String districtName;

    private String city;    
    private String state;
    private String country;
  
    
    public String getCacheKey() {
        return String.format("%s-%s-%s-%s", city, state, country);
    }

    
}
