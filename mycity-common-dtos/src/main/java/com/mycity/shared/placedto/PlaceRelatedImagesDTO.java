package com.mycity.shared.placedto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRelatedImagesDTO { 
	
	
	private String placeId;
    private String placeName;
    private String aboutPlace;
    private List<String> photoUrls;
}
