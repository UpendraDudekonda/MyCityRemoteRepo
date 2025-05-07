package com.mycity.shared.mediadto;

import java.util.List;

import lombok.Data;

@Data
public class AboutPlaceCuisineImageDTO {

	private String cuisineName;
	
	
	private List<AboutPlaceImageDTO> imageUrl;
}
