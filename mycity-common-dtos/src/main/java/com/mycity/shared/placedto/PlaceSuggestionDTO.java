package com.mycity.shared.placedto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceSuggestionDTO {
    private String placeName;
    private String aboutPlace;
    private double latitude;
    private double longitude;
    private double distanceFromRoute; // optional
    private List<String> photoUrls;
}

