package com.mycity.shared.placedto;

import java.util.List;

import com.mycity.shared.mediadto.AboutPlaceImageDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NearbyPlaceDTO {
	 private Long placeId;
	    private String placeName;
	    private double latitude;
	    private double longitude;
	    private List<AboutPlaceImageDTO> imageUrls; 
}
