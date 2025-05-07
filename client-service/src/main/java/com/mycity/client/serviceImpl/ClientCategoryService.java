package com.mycity.client.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.categorydto.CategoryImageDTO;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ClientCategoryService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String CATEGORY_SERVICE = "CATEGORY-SERVICE";  
    private static final String CATEGORY_FETCH_PATH = "/category/unique/images"; 

    public Mono<List<CategoryImageDTO>> fetchCategoriesWithImages() {
        return webClientBuilder
                .baseUrl("lb://" + CATEGORY_SERVICE)  // 🔥 Use loadbalancer URL
                .build()
                .get()
                .uri(CATEGORY_FETCH_PATH)  
                .retrieve()
                .bodyToFlux(CategoryImageDTO.class)
                .collectList();
    }
}
