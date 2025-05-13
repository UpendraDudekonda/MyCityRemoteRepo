package com.mycity.shared.categorydto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImageDTO { 
	
	public CategoryImageDTO(String categoryname, String image) {
		// TODO Auto-generated constructor stub
	}
	private String categoryName;
    private String imageUrl;
    private String placeId;
    private String placeName;
    private String placeDescription;
}
