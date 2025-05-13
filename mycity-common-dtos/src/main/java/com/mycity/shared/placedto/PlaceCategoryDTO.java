package com.mycity.shared.placedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceCategoryDTO {
	private Long placeId;
	private String placeName;
    private String placeHistory;
    private Long categoryId;
    private String categoryName;
    
    public PlaceCategoryDTO(Long placeId, Long categoryId) {
        this.placeId = placeId;
        this.categoryId = categoryId;
    }

	public PlaceCategoryDTO(String categoryName, long placeId, String placeName) {
		// TODO Auto-generated constructor stub
		this.categoryName=categoryName;
		this.placeId=placeId;
		this.placeName=placeName;
	}

}