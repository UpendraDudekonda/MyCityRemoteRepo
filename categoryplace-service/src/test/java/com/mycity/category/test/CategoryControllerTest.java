package com.mycity.category.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.mycity.category.controller.CategoryController;
import com.mycity.category.service.CategoryService;
import com.mycity.shared.categorydto.CategoryDTO;
import com.mycity.shared.categorydto.CategoryImageDTO;

import reactor.core.publisher.Mono;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    private CategoryController categoryController;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryController = new CategoryController(categoryService); // pass mock via constructor
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }


    @Test
    public void testGetCategoriesWithImages() {
        List<CategoryImageDTO> mockList = Arrays.asList(
            new CategoryImageDTO("Art", "http://example.com/image1.jpg"),
            new CategoryImageDTO("Music", "http://example.com/image2.jpg")
        );

        when(categoryService.fetchCategoriesWithImages()).thenReturn(Mono.just(mockList));

        webTestClient.get()
                .uri("/category/unique/images")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryImageDTO.class)
                .hasSize(2)
                .contains(mockList.get(0), mockList.get(1));
    }

    @Test
    public void testCategoryExists() {
        when(categoryService.categoryExists("Art")).thenReturn(true);

        ResponseEntity<Boolean> response = categoryController.categoryExists("Art");

        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    public void testCreateCategory() {
        CategoryDTO input = new CategoryDTO("Art", "Artistic category");

        when(categoryService.createCategory("Art", "Artistic category")).thenReturn(input);

        ResponseEntity<CategoryDTO> response = categoryController.createCategory(input);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Art", response.getBody().getName());
    }

    @Test
    public void testGetCategoryByName() {
        CategoryDTO mockDTO = new CategoryDTO("Music", "Musical vibes");

        when(categoryService.getCategoryByName("Music")).thenReturn(mockDTO);

        ResponseEntity<CategoryDTO> response = categoryController.getCategoryByName("Music");

        assertEquals("Musical vibes", response.getBody().getDescription());
    }

    @Test
    public void testSaveCategory() {
        CategoryDTO dto = new CategoryDTO("Travel", "Travel category");

        when(categoryService.saveCategory(dto)).thenReturn(dto);

        ResponseEntity<CategoryDTO> response = categoryController.saveCategory(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Travel", response.getBody().getName());
    }

    @Test
    public void testGetCategoryDescriptionByName() {
        when(categoryService.getDescriptionByCategoryName("Tech")).thenReturn("Technology related");

        ResponseEntity<String> response = categoryController.getCategoryDescriptionByName("Tech");

        assertEquals("Technology related", response.getBody());
    }
}
