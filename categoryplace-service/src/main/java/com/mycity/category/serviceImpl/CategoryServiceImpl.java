package com.mycity.category.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycity.category.entity.Category;
import com.mycity.category.repository.CategoryRepository;
import com.mycity.category.service.CategoryService;
import com.mycity.shared.categorydto.CategoryDTO;
import com.mycity.shared.categorydto.CategoryImageDTO;
import com.mycity.shared.categorydto.CategoryWithPlacesDTO;
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceRelatedImagesDTO;

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

	                        Mono<String> imageUrlMono = mediaService.fetchCategoryImage(categoryName);
	                        Mono<String> descriptionMono = fetchCategoryDescription(categoryName);

	                        Mono<CategoryImageDTO> dtoMono = Mono.zip(imageUrlMono, descriptionMono)
	                                .map(tuple -> new CategoryImageDTO(
	                                        categoryName,
	                                        tuple.getT1(),
	                                        placeId,
	                                        placeName,
	                                        tuple.getT2()
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
	    
	    @Override
	    public Mono<List<CategoryWithPlacesDTO>> getSingleCategoryWithPlacesAndImages(String categoryName) {
	        return Mono.fromCallable(() -> categoryRepo.findAllByNameIgnoreCase(categoryName))
	            .flatMapMany(Flux::fromIterable)
	            .flatMap(category -> {
	                String categoryId = String.valueOf(category.getCategoryId());

	                return placeService.getPlacesByCategoryId(categoryId)
	                    .flatMapMany(Flux::fromIterable)
	                    .flatMap(place ->
	                        mediaService.getImagesByPlaceId(place.getPlaceId())
	                            .map(photoUrls -> {
	                                PlaceRelatedImagesDTO dto = new PlaceRelatedImagesDTO();
	                                dto.setPlaceId(String.valueOf(place.getPlaceId()));
	                                dto.setPlaceName(place.getPlaceName());
	                                dto.setAboutPlace(place.getAboutPlace());
	                                dto.setPhotoUrls(photoUrls); // leave as-is
	                                return dto;
	                            })
	                    )
	                    .collectList()
	                    .map(placeDtos -> {
	                        CategoryWithPlacesDTO dto = new CategoryWithPlacesDTO();
	                        dto.setCategoryName(category.getName());
	                        dto.setPlaces(placeDtos);
	                        return dto;
	                    });
	            })
	            .collectList()
	            .map(this::mergeCategoriesByName); // <-- merge duplicates here
	    }
	    private List<CategoryWithPlacesDTO> mergeCategoriesByName(List<CategoryWithPlacesDTO> list) {
	        Map<String, CategoryWithPlacesDTO> map = new LinkedHashMap<>();

	        for (CategoryWithPlacesDTO dto : list) {
	            String name = dto.getCategoryName().toLowerCase();

	            if (!map.containsKey(name)) {
	                map.put(name, dto);
	            } else {
	                CategoryWithPlacesDTO existing = map.get(name);
	                List<PlaceRelatedImagesDTO> mergedPlaces = new ArrayList<>(existing.getPlaces());

	                for (PlaceRelatedImagesDTO place : dto.getPlaces()) {
	                    boolean alreadyPresent = mergedPlaces.stream()
	                        .anyMatch(p -> p.getPlaceId().equals(place.getPlaceId()));
	                    if (!alreadyPresent) mergedPlaces.add(place);
	                }

	                existing.setPlaces(mergedPlaces);
	            }
	        }

	        return new ArrayList<>(map.values());
	    }


}