package com.mycity.category.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycity.category.service.CategoryService;
import com.mycity.shared.categorydto.CategoryImageDTO;

import reactor.core.publisher.Mono;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	 	@Autowired
	    private WebClientPlaceService placeService;  

	    @Autowired
	    private WebClientMediaService mediaService;  

	    @Override
	    public Mono<List<CategoryImageDTO>> fetchCategoriesWithImages() {
	        return placeService.fetchPlaceCategories()
	                .flatMap(place -> {
	                    String categoryName = place.getPlaceCategory();
	                    Long placeId = place.getPlaceId();
	                    String fplaceId = String.valueOf(placeId);
	                    String placeName = place.getPlaceName();
	                    String placeHistory = place.getPlaceHistory();

	                    return mediaService.fetchCategoryImage(categoryName)
	                            .map(imageUrl -> new CategoryImageDTO(categoryName, imageUrl, fplaceId, placeName, placeHistory));
	                })
	                .distinct(CategoryImageDTO::getCategoryName) 
	                .collectList();
	    }


	

}
