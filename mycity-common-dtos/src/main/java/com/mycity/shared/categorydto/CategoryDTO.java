package com.mycity.shared.categorydto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

	private Long categoryId;

	private String name; // Example: "Historical", "Adventure", "Wildlife"

	private String description;
}
