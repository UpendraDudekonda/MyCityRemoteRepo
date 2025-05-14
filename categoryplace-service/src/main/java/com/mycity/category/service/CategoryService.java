package com.mycity.category.service;

import java.util.List;

import com.mycity.shared.categorydto.CategoryDTO;
import com.mycity.shared.categorydto.CategoryImageDTO;
import com.mycity.shared.categorydto.CategoryWithPlacesDTO;

import reactor.core.publisher.Mono;

public interface CategoryService {

	Mono<List<CategoryImageDTO>> fetchCategoriesWithImages();

	CategoryDTO getCategoryByName(String categoryName);

	CategoryDTO saveCategory(CategoryDTO categoryDTO);

	CategoryDTO createCategory(String name, String description);

	boolean categoryExists(String name);

	Mono<String> fetchCategoryDescription(String categoryName);

	String getDescriptionByCategoryName(String categoryName);


	  Mono<List<CategoryWithPlacesDTO>> getSingleCategoryWithPlacesAndImages(String categoryName);

}
