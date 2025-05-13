package com.mycity.category.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.mycity.category.entity.Category;
import com.mycity.category.repository.CategoryRepository;
import com.mycity.category.serviceImpl.CategoryServiceImpl;
import com.mycity.category.serviceImpl.WebClientMediaService;
import com.mycity.category.serviceImpl.WebClientPlaceService;
import com.mycity.shared.categorydto.CategoryDTO;
import com.mycity.shared.categorydto.CategoryImageDTO;
import com.mycity.shared.placedto.PlaceCategoryDTO;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepo;

    @Mock
    private WebClientPlaceService placeService;

    @Mock
    private WebClientMediaService mediaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCategoryExists_ReturnsTrue() {
        when(categoryRepo.existsByNameIgnoreCase("Historic")).thenReturn(true);
        boolean exists = categoryService.categoryExists("Historic");
        assertTrue(exists);
    }

    @Test
    public void testGetCategoryByName_ReturnsCategoryDTO() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setName("Temples");
        category.setDescription("Spiritual locations");

        when(categoryRepo.findByNameIgnoreCase("Temples")).thenReturn(Optional.of(category));

        CategoryDTO dto = categoryService.getCategoryByName("Temples");

        assertNotNull(dto);
        assertEquals("Temples", dto.getName());
        assertEquals("Spiritual locations", dto.getDescription());
    }

    @Test
    public void testCreateCategory_Success() {
        Category toSave = new Category();
        toSave.setName("Nature");
        toSave.setDescription("Natural sights");

        Category saved = new Category();
        saved.setCategoryId(2L);
        saved.setName("Nature");
        saved.setDescription("Natural sights");

        when(categoryRepo.existsByNameIgnoreCase("Nature")).thenReturn(false);
        when(categoryRepo.save(any(Category.class))).thenReturn(saved);

        CategoryDTO result = categoryService.createCategory("Nature", "Natural sights");

        assertNotNull(result);
        assertEquals("Nature", result.getName());
        assertEquals("Natural sights", result.getDescription());
    }

    @Test
    public void testSaveCategory_Success() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Adventure");
        dto.setDescription("Thrilling experiences");
        dto.setPlaceId(1L);
        dto.setPlaceName("Mountain Climb");

        Category saved = new Category();
        saved.setCategoryId(3L);
        saved.setName("Adventure");
        saved.setDescription("Thrilling experiences");
        saved.setPlaceId(1L);
        saved.setPlaceName("Mountain Climb");

        when(categoryRepo.save(any(Category.class))).thenReturn(saved);

        CategoryDTO result = categoryService.saveCategory(dto);

        assertNotNull(result);
        assertEquals("Adventure", result.getName());
        assertEquals("Thrilling experiences", result.getDescription());
    }

    @Test
    public void testFetchCategoryDescription_ReturnsDescription() {
        when(categoryRepo.findDescriptionsByNameIgnoreCase("Cultural")).thenReturn(List.of("Cultural heritage"));

        String description = categoryService.fetchCategoryDescription("Cultural").block();

        assertEquals("Cultural heritage", description);
    }

    @Test
    public void testFetchCategoriesWithImages_ReturnsUniqueCategoryImageDTOs() {
        // Duplicate category "Cultural", only one should be included
        PlaceCategoryDTO place1 = new PlaceCategoryDTO("Cultural", 101L, "Heritage Museum");
        PlaceCategoryDTO place2 = new PlaceCategoryDTO("Cultural", 102L, "Folk Center");
        PlaceCategoryDTO place3 = new PlaceCategoryDTO("Nature", 103L, "National Park");

        when(placeService.fetchPlaceCategories()).thenReturn(Flux.just(place1, place2, place3));
        when(mediaService.fetchCategoryImage("Cultural")).thenReturn(Mono.just("img-cultural"));
        when(mediaService.fetchCategoryImage("Nature")).thenReturn(Mono.just("img-nature"));

        when(categoryRepo.findDescriptionsByNameIgnoreCase("Cultural")).thenReturn(List.of("Heritage sites"));
        when(categoryRepo.findDescriptionsByNameIgnoreCase("Nature")).thenReturn(List.of("Nature exploration"));

        List<CategoryImageDTO> result = categoryService.fetchCategoriesWithImages().block();

        assertNotNull(result);
        assertEquals(2, result.size());

        CategoryImageDTO cultural = result.stream().filter(dto -> dto.getCategoryName().equals("Cultural")).findFirst().orElse(null);
        CategoryImageDTO nature = result.stream().filter(dto -> dto.getCategoryName().equals("Nature")).findFirst().orElse(null);

        assertNotNull(cultural);
        assertEquals("img-cultural", cultural.getImageUrl());
        assertEquals("Heritage sites", cultural.getPlaceDescription());

        assertNotNull(nature);
        assertEquals("img-nature", nature.getImageUrl());
        assertEquals("Nature exploration", nature.getPlaceDescription());
    }
}
