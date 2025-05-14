package com.mycity.category.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycity.category.entity.Category;
import com.mycity.category.repository.CategoryRepository;
import com.mycity.category.service.CategoryService;
import com.mycity.shared.categorydto.CategoryDTO;
import com.mycity.shared.categorydto.CategoryImageDTO;
import com.mycity.shared.placedto.PlaceCategoryDTO;

import reactor.core.publisher.Flux;
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
	                .collectList()
	                .flatMap(places -> {
	                    Set<String> seenCategories = new HashSet<>();
	                    List<PlaceCategoryDTO> uniquePlaces = new ArrayList<>();

	                    // Filter unique categories using a for-loop
	                    for (PlaceCategoryDTO place : places) {
	                        String normalized = place.getCategoryName().trim().toLowerCase();
	                        if (!seenCategories.contains(normalized)) {
	                            seenCategories.add(normalized);
	                            uniquePlaces.add(place);
	                        }
	                    }

	                    // Now map each unique place to CategoryImageDTO
	                    List<Mono<CategoryImageDTO>> dtoMonos = new ArrayList<>();
	                    for (PlaceCategoryDTO place : uniquePlaces) {
	                        String categoryName = place.getCategoryName();
	                        String placeId = String.valueOf(place.getPlaceId());
	                        String placeName = place.getPlaceName();

	                        System.err.println("the extracted category is ....." + categoryName);
	                        System.err.println("the placeId is: " + placeId + ", placeName: " + placeName);

	                        // Fetch the image URL and description
	                        Mono<String> imageUrlMono = mediaService.fetchCategoryImage(categoryName)
	                                .doOnNext(imageUrl -> {
	                                    if (imageUrl == null || imageUrl.isEmpty()) {
	                                        System.out.println("Image URL is empty for category: " + categoryName);
	                                    } else {
	                                        System.out.println("Fetched image URL for category " + categoryName + ": " + imageUrl);
	                                    }
	                                });

	                        Mono<String> descriptionMono = fetchCategoryDescription(categoryName)
	                                .doOnNext(description -> {
	                                    if (description == null || description.isEmpty()) {
	                                        System.out.println("Description is empty for category: " + categoryName);
	                                    } else {
	                                        System.out.println("Fetched description for category " + categoryName + ": " + description);
	                                    }
	                                });

	                        // Combine the image URL and description into CategoryImageDTO
	                        Mono<CategoryImageDTO> dtoMono = Mono.zip(imageUrlMono, descriptionMono)
	                                .doOnNext(tuple -> {
	                                    System.out.println("Inside zip: " +
	                                            "Image URL: " + tuple.getT1() +
	                                            ", Description: " + tuple.getT2());
	                                })
	                                .map(tuple -> new CategoryImageDTO(
	                                        categoryName,
	                                        tuple.getT1(), // image URL
	                                        placeId,
	                                        placeName,
	                                        tuple.getT2() // description
	                                ));

	                        dtoMonos.add(dtoMono);
	                    }

	                    return Flux.concat(dtoMonos).collectList();
	                });
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
