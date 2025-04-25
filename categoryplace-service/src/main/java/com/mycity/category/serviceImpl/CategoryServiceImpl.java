package com.mycity.category.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.category.service.CategoryService;
import com.mycity.shared.categorydto.CategoryImageDTO;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String PLACE_SERVICE_URL = "http://PLACE-SERVICE/place/places/categories";
    private static final String MEDIA_SERVICE_URL = "http://MEDIA-SERVICE/api/media/cover-image?category=";

    public List<CategoryImageDTO> fetchCategoriesWithImages() {
        List<String> categories = webClientBuilder.build()
            .get()
            .uri(PLACE_SERVICE_URL)
            .retrieve()
            .bodyToFlux(String.class)
            .collectList()
            .block();

        return categories.stream().map(category -> {
            String imageUrl = webClientBuilder.build()
                .get()
                .uri(MEDIA_SERVICE_URL + category)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return new CategoryImageDTO(category, imageUrl);
        }).collect(Collectors.toList());
    }
}
