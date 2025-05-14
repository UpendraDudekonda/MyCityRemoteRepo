package com.mycity.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.category.service.CategoryService;
import com.mycity.shared.categorydto.CategoryDTO;
import com.mycity.shared.categorydto.CategoryImageDTO;
import com.mycity.shared.categorydto.CategoryWithPlacesDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/category")
public class CategoryController {

	private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/unique/images")
    public Mono<ResponseEntity<List<CategoryImageDTO>>> getCategoriesWithImages() {
    	
        return categoryService.fetchCategoriesWithImages()
            .map(ResponseEntity::ok);
    }
    
    @GetMapping("/exists")
    public ResponseEntity<Boolean> categoryExists(@RequestParam String name) {
        boolean exists = categoryService.categoryExists(name);
        return ResponseEntity.ok(exists);
    }

    // Create a new category
    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO created = categoryService.createCategory(categoryDTO.getName(), categoryDTO.getDescription());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get category by name
    @GetMapping("/categorybyname/{categoryName}")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String categoryName) {
        CategoryDTO category = categoryService.getCategoryByName(categoryName);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/category-by-name/{categoryName}")
    public Mono<ResponseEntity<List<CategoryWithPlacesDTO>>> getCategoriesWithPlacesAndImages(@PathVariable String categoryName) {
        return categoryService.getSingleCategoryWithPlacesAndImages(categoryName)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }




    // Save (or update) category directly
    @PostMapping("/save")
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO saved = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/desc/{categoryName}")
    public ResponseEntity<String> getCategoryDescriptionByName(@PathVariable String categoryName) {
        String description = categoryService.getDescriptionByCategoryName(categoryName);
        return ResponseEntity.ok(description);
    }


    

}
