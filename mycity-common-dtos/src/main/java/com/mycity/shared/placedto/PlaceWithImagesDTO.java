package com.mycity.shared.placedto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceWithImagesDTO {

    private long placeId;
    private String placeName;
    private String aboutPlace;
    private String placeHistory;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Double rating;
    private String placeDistrict;
    private Double latitude;
    private Double longitude;
    
    private List<String> photoUrls; // ✅ List of all images
    
    private String placeCategoryDescription;
    private LocalDate postedOn;
    private String categoryName;
}
