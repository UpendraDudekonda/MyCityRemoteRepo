package com.mycity.place.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycity.shared.categorydto.CategoryDTO;

@Service
public class WebClientCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(WebClientCategoryService.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String CATEGORY_SERVICE = "CATEGORY-SERVICE";  
    private static final String CATEGORY_EXISTS_PATH = "/category/exists?name={categoryName}";
    private static final String CATEGORY_CREATE_PATH = "/category/create";
    private static final String CATEGORY_BY_NAME_PATH = "/category/categorybyname/{categoryName}";
    private static final String CATEGORY_SAVE_PATH = "/category/save"; 
   
    public boolean checkCategoryExists(String categoryName) {
        try {
            Integer count = webClientBuilder.baseUrl("lb://" + CATEGORY_SERVICE)  
                    .build()
                    .get()
                    .uri(CATEGORY_EXISTS_PATH, categoryName)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();  // Blocking is necessary here, but ensure you handle exceptions properly

            return count != null && count > 0;
        } catch (WebClientResponseException e) {
            logger.error("Error occurred while checking if category exists: {}", e.getMessage(), e);
            return false; 
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return false;  // Return false or handle as needed
        }
    }

    public CategoryDTO createCategory(String categoryName, String categoryDescription) {
        try {
            return webClientBuilder.baseUrl("lb://" + CATEGORY_SERVICE)  
                    .build()
                    .post()
                    .uri(CATEGORY_CREATE_PATH)
                    .bodyValue(new CategoryDTO(categoryName, categoryDescription))
                    .retrieve()
                    .bodyToMono(CategoryDTO.class)
                    .block();  // Blocking is necessary, but error handling should be implemented
        } catch (WebClientResponseException e) {
            logger.error("Error occurred while creating category: {}", e.getMessage(), e);
            return null;  // Or return a fallback value
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return null;  // Handle as needed
        }
    }

    public CategoryDTO getCategoryByName(String categoryName) {
        try {
            return webClientBuilder.baseUrl("lb://" + CATEGORY_SERVICE)  
                    .build()
                    .get()
                    .uri(CATEGORY_BY_NAME_PATH, categoryName)
                    .retrieve()
                    .bodyToMono(CategoryDTO.class)
                    .block();  // Blocking is necessary, but consider returning Mono for better async handling
        } catch (WebClientResponseException e) {
            logger.error("Error occurred while retrieving category by name: {}", e.getMessage(), e);
            return null;  // Or handle as needed
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return null;  // Handle as needed
        }
    }

    public CategoryDTO saveCategory(CategoryDTO category) {
        try {
            return webClientBuilder.baseUrl("lb://" + CATEGORY_SERVICE)  
                    .build()
                    .post()
                    .uri(CATEGORY_SAVE_PATH)
                    .bodyValue(category)
                    .retrieve()
                    .bodyToMono(CategoryDTO.class)
                    .block();  // Blocking is necessary, but ensure error handling is in place
        } catch (WebClientResponseException e) {
            logger.error("Error occurred while saving category: {}", e.getMessage(), e);
            return null;  // Or return a fallback value
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return null;  // Handle as needed
        }
    }

    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        try {
            return webClientBuilder.baseUrl("lb://" + CATEGORY_SERVICE)
                    .build()
                    .post()
                    .uri(CATEGORY_SAVE_PATH)
                    .bodyValue(categoryDTO)
                    .retrieve()
                    .bodyToMono(CategoryDTO.class)
                    .block();  // Blocking to get the created CategoryDTO
        } catch (WebClientResponseException e) {
            logger.error("Error occurred while adding category: {}", e.getMessage(), e);
            return null;  // Or you can throw a custom exception if you prefer
        } catch (Exception e) {
            logger.error("Unexpected error occurred while adding category: {}", e.getMessage(), e);
            return null;
        }
    }

}
