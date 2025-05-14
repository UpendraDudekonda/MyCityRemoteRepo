package com.mycity.shared.categorydto;

import java.util.List;

import com.mycity.shared.placedto.PlaceRelatedImagesDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithPlacesDTO {
	
	
    private String categoryName;
    private List<PlaceRelatedImagesDTO> places;
}
