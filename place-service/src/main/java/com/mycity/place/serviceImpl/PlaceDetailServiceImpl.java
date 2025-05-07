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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.place.entity.Place;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceDetailService;
import com.mycity.shared.locationdto.LocationDTO;
import com.mycity.shared.placedto.AboutPlaceResponseDTO;
import com.mycity.shared.placedto.UserGalleryDTO;
import com.mycity.shared.reviewdto.ReviewDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class PlaceDetailServiceImpl implements PlaceDetailService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PlaceDetailServiceImpl.class);

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private WebClientMediaService mediaService; // Autowiring mediaService to reuse its methods
    
    @Autowired
    private WebClientReviewService reviewService;
	
	@Autowired
	private HttpServletRequest servletRequest;
	
	@Autowired
	public WebClient.Builder webClientBuilder;
	
    @Autowired
    private WebClientLocationService locationService;

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
        about.setOpeningTime(place.getTimeZone().getOpeningTime());
        about.setCloingTime(place.getTimeZone().getClosingTime()); // Fixing: You set opening time twice
        about.setRating(place.getRating());

        try {
            // Fetching data from different services asynchronously
            CompletableFuture<List<String>> imagesFuture = mediaService.getImageUrlsForPlace(placeId);
            CompletableFuture<List<ReviewDTO>> reviewsFuture = reviewService.fetchReviews(placeId);
            CompletableFuture<LocationDTO> locationFuture = locationService.fetchLocationFromLocationService(placeId);

            // Blocking to get the results from futures
            List<String> images = imagesFuture.get();
//            List<ReviewDTO> reviews = reviewsFuture.get();
//            LocationDTO location = locationFuture.get();

            // ✅ Set image list in AboutPlaceResponseDTO
            about.setPlaceRelatedImages(images);

            data.put("images", images);
//            data.put("reviews", reviews);
//            data.put("location", location); 
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching data from services: {}", e.getMessage());

            // Default fallback if error occurs
            about.setPlaceRelatedImages(new ArrayList<>());
            data.put("images", new ArrayList<>());
            data.put("reviews", new ArrayList<>());
            data.put("location", null);
        }

        data.put("about", about); // ⬅️ Make sure this is added *after* setting all fields
        aboutMapSection.put("data", data);
        return aboutMapSection;
    }

}
