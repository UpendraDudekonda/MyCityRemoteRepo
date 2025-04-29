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

import com.mycity.place.entity.Place;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceDetailService;
import com.mycity.shared.locationdto.LocationDTO;
import com.mycity.shared.placedto.AboutPlaceResponseDTO;
import com.mycity.shared.reviewdto.ReviewDTO;

@Service
public class AboutPlaceDetailServiceImpl implements PlaceDetailService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AboutPlaceDetailServiceImpl.class);

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private WebClientMediaService mediaService; // Autowiring mediaService to reuse its methods
    
    @Autowired
    private WebClientReviewService reviewService;
    

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
        about.setLocalCuisines(place.getLocalCuisines());
        about.setNearByHotels(place.getNearByHotels());
        about.setLatitude(place.getCoordinate().getLatitude());
        about.setLongitude(place.getCoordinate().getLongitude());

        try {
            // Fetching data from different services asynchronously
            CompletableFuture<List<String>> imagesFuture = mediaService.getImageUrlsForPlace(placeId);
            CompletableFuture<List<ReviewDTO>> reviewsFuture = reviewService.fetchReviews(placeId);
         

            // Blocking to get the results from futures
            List<String> images = imagesFuture.get();
            List<ReviewDTO> reviews = reviewsFuture.get();
        

            // ✅ Set image list in AboutPlaceResponseDTO
            about.setPlaceRelatedImages(images);
            about.setReviews(reviews);

//            data.put("images", images);
//            data.put("reviews", reviews);
//            data.put("location", location); 
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching data from services: {}", e.getMessage());

            // Default fallback if error occur
            
            data.put("images", new ArrayList<>());
            data.put("reviews", new ArrayList<>());
           
        }

        data.put("about", about); // ⬅️ Make sure this is added *after* setting all fields
        aboutMapSection.put("data", data);
        return aboutMapSection;
    }

}
