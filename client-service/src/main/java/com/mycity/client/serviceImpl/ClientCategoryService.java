package com.mycity.client.serviceImpl;

import com.mycity.shared.categorydto.CategoryImageDTO;
import com.mycity.shared.categorydto.CategoryWithPlacesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ClientCategoryService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String CATEGORY_SERVICE = "CATEGORY-SERVICE";
    private static final String CATEGORY_FETCH_PATH = "/category/unique/images";
    private static final String CATEGORY_BY_NAME_PATH = "/category/category-by-name/{categoryName}";

    public Mono<List<CategoryImageDTO>> fetchCategoriesWithImages() {
        return webClientBuilder
                .baseUrl("lb://" + CATEGORY_SERVICE)
                .build()
                .get()
                .uri(CATEGORY_FETCH_PATH)
                .retrieve()
                .bodyToFlux(CategoryImageDTO.class)
                .collectList();
    }

    public Mono<List<CategoryWithPlacesDTO>> fetchCategoryWithPlacesAndImages(String categoryName) {
        return webClientBuilder
                .baseUrl("lb://" + CATEGORY_SERVICE)
                .build()
                .get()
                .uri(CATEGORY_BY_NAME_PATH, categoryName)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CategoryWithPlacesDTO>>() {});
    }
}
