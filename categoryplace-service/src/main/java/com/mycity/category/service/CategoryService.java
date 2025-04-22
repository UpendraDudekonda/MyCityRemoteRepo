package com.mycity.category.service;

import java.util.List;

import com.mycity.shared.categorydto.CategoryImageDTO;

public interface CategoryService {

	List<CategoryImageDTO> fetchCategoriesWithImages();

}
