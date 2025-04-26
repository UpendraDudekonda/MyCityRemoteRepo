package com.mycity.category.service;

import java.util.List;

import com.mycity.shared.categorydto.CategoryImageDTO;

import reactor.core.publisher.Mono;

public interface CategoryService {

	Mono<List<CategoryImageDTO>> fetchCategoriesWithImages();

}
