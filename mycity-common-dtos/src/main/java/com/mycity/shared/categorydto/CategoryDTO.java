package com.mycity.shared.categorydto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

	 public CategoryDTO(String name, String description) {
	        this.name = name;
	        this.description = description;
	    }

		private Long categoryId;

	    private Long placeId;    // You might consider using `placeId` only if needed, it's optional in the `Category` context.
	    
	    private String placeName;

	    private String name;  // Name of the category

	    private String description; // Description of the category
}
