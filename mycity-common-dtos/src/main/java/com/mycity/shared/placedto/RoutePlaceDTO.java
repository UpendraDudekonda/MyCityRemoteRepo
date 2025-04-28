package com.mycity.shared.placedto;

import java.util.List;


import com.mycity.shared.tripplannerdto.CoordinateDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutePlaceDTO {

    private String placeId;
    private String name;
    private CoordinateDto coordinate;
    private String address;
    private String description;
    private List<String> types;
    private Double rating;
    private List<String> photoUrls;
    private Double distanceFromRoute; 
    private String operatingHours;   
}

