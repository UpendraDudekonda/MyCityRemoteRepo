package com.mycity.category.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycity.category.entity.Category;
import com.mycity.category.repository.CategoryRepository;
import com.mycity.category.service.CategoryService;
import com.mycity.shared.categorydto.CategoryDTO;
import com.mycity.shared.categorydto.CategoryImageDTO;

import reactor.core.publisher.Mono;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	 	@Autowired
	    private WebClientPlaceService placeService;  

	    @Autowired
	    private WebClientMediaService mediaService;  
	    
	    @Autowired
	    private CategoryRepository categoryRepo;

	    @Override
	    public Mono<List<CategoryImageDTO>> fetchCategoriesWithImages() {
	        return placeService.fetchPlaceCategories()
	                .flatMap(place -> {
	                    String categoryName = place.getCategoryName();
	                    Long placeId = place.getPlaceId();
	                    String fplaceId = String.valueOf(placeId);
	                    String placeName = place.getPlaceName();

	                    Mono<String> imageUrlMono = mediaService.fetchCategoryImage(categoryName);
	                    Mono<String> descriptionMono = fetchCategoryDescription(categoryName);

	                    return Mono.zip(imageUrlMono, descriptionMono)
	                            .map(tuple -> {
	                                String imageUrl = tuple.getT1();
	                                String description = tuple.getT2();
	                                return new CategoryImageDTO(categoryName, imageUrl, fplaceId, placeName, description);
	                            });
	                })
	                .distinct(CategoryImageDTO::getCategoryName)
	                .collectList();
	    }

	    @Override
	    public boolean categoryExists(String categoryName) {
	        return categoryRepo.existsByNameIgnoreCase(categoryName);
	    }

	    @Override
	    public CategoryDTO getCategoryByName(String categoryName) {
	        Category category = categoryRepo.findByNameIgnoreCase(categoryName)
	                .orElseThrow(() -> new IllegalArgumentException("Category not found with name: " + categoryName));
	        return mapToDTO(category);
	    }


	    @Override
	    public CategoryDTO createCategory(String name, String description) {
	        if (categoryRepo.existsByNameIgnoreCase(name)) {
	            throw new IllegalArgumentException("Category already exists with name: " + name);
	        }
	        Category category = new Category();
	        category.setName(name);
	        category.setDescription(description);
	        Category saved = categoryRepo.save(category);
	        return mapToDTO(saved);
	    }

	    @Override
	    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
	        Category category = new Category();
	        category.setName(categoryDTO.getName());
	        category.setDescription(categoryDTO.getDescription());

	      
	        category.setPlaceId(categoryDTO.getPlaceId());
	        category.setPlaceName(categoryDTO.getPlaceName());

	        if (categoryDTO.getCategoryId() != null) {
	            category.setCategoryId(categoryDTO.getCategoryId());
	        }

	        Category saved = categoryRepo.save(category);
	        return mapToDTO(saved);
	    }


	    // Utility: Entity ➔ DTO
	    private CategoryDTO mapToDTO(Category category) {
	        CategoryDTO dto = new CategoryDTO();
	        dto.setCategoryId(category.getCategoryId());
	        dto.setName(category.getName());
	        dto.setDescription(category.getDescription());
	        return dto;
	    }
	    
	    @Override
	    public Mono<String> fetchCategoryDescription(String categoryName) {
	        return Mono.fromCallable(() -> {
	            List<String> descriptions = categoryRepo.findDescriptionsByNameIgnoreCase(categoryName);
	            if (descriptions.isEmpty()) {
	                throw new RuntimeException("Category not found: " + categoryName);
	            }
	            return descriptions.get(0); 
	        });
	    }

	    public String getDescriptionByCategoryName(String categoryName) {
	        List<Category> categories = categoryRepo.findAllByName(categoryName);

	        if (!categories.isEmpty()) {
	            return categories.get(0).getDescription(); // Return description of the first match
	        } else {
	            throw new IllegalArgumentException("Category with name '" + categoryName + "' not found.");
	        }
	    }





}
