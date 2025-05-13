package com.mycity.category.test;

import com.mycity.category.entity.Category;
import com.mycity.category.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    public void setUp() {
        category1 = new Category();
        category1.setName("Parks");
        category1.setDescription("Public recreational spaces for relaxation.");

        category2 = new Category();
        category2.setName("Monuments");
        category2.setDescription("Historical sites and statues.");

        category3 = new Category();
        category3.setName("Museums");
        category3.setDescription("Institutions that preserve and exhibit art or science.");

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
    }
    
    @Test
    public void testFindByName() {
        Category result = categoryRepository.findByName("Parks");
        assertNotNull(result);
        assertEquals("Parks", result.getName());
        assertEquals("Public recreational spaces for relaxation.", result.getDescription());
    }

    @Test
    public void testExistsByNameIgnoreCase() {
        boolean exists = categoryRepository.existsByNameIgnoreCase("parks");
        assertTrue(exists);
    }

    @Test
    public void testFindByNameIgnoreCase() {
        Optional<Category> result = categoryRepository.findByNameIgnoreCase("MONUMENTS");
        assertTrue(result.isPresent());
        assertEquals("Monuments", result.get().getName());
    }

    @Test
    public void testFindDescriptionsByNameIgnoreCase() {
        List<String> descriptions = categoryRepository.findDescriptionsByNameIgnoreCase("parks");
        assertEquals(1, descriptions.size());
        assertEquals("Public recreational spaces for relaxation.", descriptions.get(0));
    }

    @Test
    public void testFindAllByName() {
        List<Category> categories = categoryRepository.findAllByName("Parks");
        assertEquals(1, categories.size());
        assertEquals("Parks", categories.get(0).getName());
    }

    @Test
    public void testSaveCategory() {
        Category newCategory = new Category();
        newCategory.setName("Beaches");
        newCategory.setDescription("Coastal areas for swimming and sunbathing.");

        Category savedCategory = categoryRepository.save(newCategory);

        assertNotNull(savedCategory);
        assertEquals("Beaches", savedCategory.getName());
        assertEquals("Coastal areas for swimming and sunbathing.", savedCategory.getDescription());
    }
}
