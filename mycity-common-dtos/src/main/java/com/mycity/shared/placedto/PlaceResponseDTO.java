package com.mycity.shared.placedto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class PlaceResponseDTO {
    private Long placeId;
    private String placeName;
    private String aboutPlace;
    private String placeHistory;
    private String placeCategory;
    private String placeDistrict;
    private Double rating;

    private Double latitude;
    private Double longitude;

    private LocalTime openingTime;
    private LocalTime closingTime;
    
//    private CategoryDTO categoryDetails;
    
    private Long categoryId;
    private String categoryName;
}