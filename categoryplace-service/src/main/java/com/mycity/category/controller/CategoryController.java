package com.mycity.category.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.category.service.CategoryService;
import com.mycity.shared.categorydto.CategoryImageDTO;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/images")
    public ResponseEntity<List<CategoryImageDTO>> getCategoriesWithImages() {
        List<CategoryImageDTO> categories = categoryService.fetchCategoriesWithImages();
        return ResponseEntity.ok(categories);
    }
}
