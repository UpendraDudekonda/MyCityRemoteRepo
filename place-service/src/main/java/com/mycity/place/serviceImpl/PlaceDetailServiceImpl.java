package com.mycity.place.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.place.entity.Place;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceDetailService;
import com.mycity.shared.locationdto.LocationDTO;
import com.mycity.shared.placedto.AboutPlaceResponseDTO;
import com.mycity.shared.reviewdto.ReviewDTO;

@Service
public class PlaceDetailServiceImpl implements PlaceDetailService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PlaceDetailServiceImpl.class);
    private final WebClient.Builder webClientBuilder;
    private static final String IMAGE_SERVICE = "IMAGE-SERVICE";
    private static final String IMAGE_PATH = "/images/about/";
//    private static final String REVIEW_SERVICE = "REVIEW-SERVICE";
//    private static final String REVIEW_PATH = "/reviews/";
//    private static final String LOCATION_SERVICE = "LOCATION-SERVICE";
//    private static final String LOCATION_PATH = "/locations/";

    @Autowired
    private PlaceRepository placeRepository;

    public PlaceDetailServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Map<String, Object> getPlaceDetails(Long placeId) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> sections = new ArrayList<>();

        Place place = placeRepository.findById(placeId).orElse(null);
        if (place == null) {
            return null; // Handle place not found
        }

        sections.add(createAboutAndMapSection(placeId, place));

        response.put("sections", sections);
        return response;
    }

    private Map<String, Object> createAboutAndMapSection(Long placeId, Place place) {
        Map<String, Object> aboutMapSection = new HashMap<>();
        aboutMapSection.put("sectionId", "aboutAndMap");
        aboutMapSection.put("title", "About and Map");
        Map<String, Object> data = new HashMap<>();

        AboutPlaceResponseDTO about = new AboutPlaceResponseDTO();
        about.setPlaceId(place.getPlaceId());
        about.setName(place.getPlaceName());
        about.setAbout(place.getAboutPlace());
        about.setHistory(place.getPlaceHistory());
//        about.setTimezone(place.getTimeZone());
//        about.setPlaceRelatedImages(place.getImages());
        // Set other necessary fields from Place to AboutPlaceResponseDTO

        data.put("about", about);

        try {
            data.put("images", fetchImages(placeId).get());
//            data.put("reviews", fetchReviews(placeId).get());
//            data.put("location", fetchLocationFromLocationService(placeId).get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching data from services: {}", e.getMessage());
            // Handle error, maybe return default values or nulls
            data.put("images", new ArrayList<>());
            data.put("reviews", new ArrayList<>());
            data.put("location", null);
        }

        aboutMapSection.put("data", data);
        return aboutMapSection;
    }

    private CompletableFuture<List<String>> fetchImages(Long placeId) {
        return webClientBuilder.build().get().uri("lb://" + IMAGE_SERVICE + IMAGE_PATH + placeId).retrieve()
                .bodyToFlux(String.class).collectList().toFuture();
    }

//    private CompletableFuture<List<ReviewDTO>> fetchReviews(Long placeId) {
//        return webClientBuilder.build().get().uri("lb://" + REVIEW_SERVICE + REVIEW_PATH + placeId).retrieve()
//                .bodyToFlux(ReviewDTO.class).collectList().toFuture();
//    }
//
//    private CompletableFuture<LocationDTO> fetchLocationFromLocationService(Long placeId) {
//        return webClientBuilder.build().get().uri("lb://" + LOCATION_SERVICE + LOCATION_PATH + placeId).retrieve()
//                .bodyToMono(LocationDTO.class).toFuture();
//    }
}