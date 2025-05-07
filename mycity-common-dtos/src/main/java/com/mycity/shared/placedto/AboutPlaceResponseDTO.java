package com.mycity.shared.placedto;

import java.time.LocalTime;
import java.util.List;

import com.mycity.shared.mediadto.AboutPlaceCuisineImageDTO;
import com.mycity.shared.mediadto.AboutPlaceImageDTO;
import com.mycity.shared.reviewdto.ReviewDTO; // Assuming you have ReviewDT

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
public class AboutPlaceResponseDTO {

	private Long placeId;
    private String name;
    private String about;
    private String history;
    private LocalTime openingTime;
    private LocalTime cloingTime;
    private double latitude;
    private double longitude;
    private double rating;
    private List<AboutPlaceImageDTO> placeRelatedImages;
    private List<ReviewDTO> reviews;
    private List<AboutPlaceCuisineImageDTO> localCuisines;
    private List<NearbyPlaceDTO> nearByPlaces;
    private List<AboutPlaceEventDTO> events;
}