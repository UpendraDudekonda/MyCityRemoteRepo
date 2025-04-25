package com.mycity.shared.placedto;

import java.time.LocalTime;
import java.util.List;

import com.mycity.shared.timezonedto.TimezoneDTO;
import com.mycity.shared.reviewdto.ReviewDTO; // Assuming you have ReviewDT
import com.mycity.shared.locationdto.LocationDTO; // Assuming you have LocationDTO

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
    private double rating;
    private List<String> placeRelatedImages;
    private List<ReviewDTO> reviews;
    private LocationDTO location;
}