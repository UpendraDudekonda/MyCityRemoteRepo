package com.mycity.place.serviceImpl;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycity.place.entity.LocalCuisine;
import com.mycity.place.entity.Place;
import com.mycity.place.exception.EventServiceUnavailableException;
import com.mycity.place.exception.MediaServiceUnavailableException;
import com.mycity.place.exception.PlaceNotFoundException;
import com.mycity.place.exception.ReviewServiceUnavailableException;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceDetailService;

import com.mycity.shared.mediadto.AboutPlaceCuisineImageDTO;
import com.mycity.shared.mediadto.AboutPlaceImageDTO;
import com.mycity.shared.placedto.AboutPlaceEventDTO;

import com.mycity.shared.placedto.AboutPlaceResponseDTO;
import com.mycity.shared.placedto.NearbyPlaceDTO;
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
	
	@Autowired
	private WebClientEventService eventService;
	
	

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
	    about.setCloingTime(place.getTimeZone().getClosingTime());
	    about.setRating(place.getRating());
	    about.setLatitude(place.getCoordinate().getLatitude());
	    about.setLongitude(place.getCoordinate().getLongitude());

	    try {
	        // Fetching data from different services asynchronously
	        CompletableFuture<List<AboutPlaceImageDTO>> imagesFuture = mediaService.getImagesForPlace(placeId);
	        CompletableFuture<List<ReviewDTO>> reviewsFuture = reviewService.fetchReviews(placeId);
	        CompletableFuture<List<AboutPlaceEventDTO>> eventFuture = eventService.fetchEvents(placeId);

	        List<AboutPlaceImageDTO> images;
	        List<ReviewDTO> reviews;
	        List<AboutPlaceEventDTO> events;

	        try {
	            images = imagesFuture.get();
	        } catch (ExecutionException e) {
	            Throwable cause = e.getCause() != null ? e.getCause() : e;
	            logger.error("Media service error for placeId {}: {}", placeId, cause.getMessage());
	            throw new MediaServiceUnavailableException("Failed to fetch media for placeId: " + placeId, cause);
	        }

	        try {
	            reviews = reviewsFuture.get();
	        } catch (ExecutionException e) {
	            Throwable cause = e.getCause() != null ? e.getCause() : e;
	            logger.error("Review service error for placeId {}: {}", placeId, cause.getMessage());
	            throw new ReviewServiceUnavailableException("Failed to fetch reviews for placeId: " + placeId, cause);
	        }


	        try {
	            events = eventFuture.get();
	        } catch (ExecutionException e) {
	            Throwable cause = e.getCause() != null ? e.getCause() : e;
	            logger.error("Event service error for placeId {}: {}", placeId, cause.getMessage());
	            throw new EventServiceUnavailableException("Failed to fetch events for placeId: " + placeId, cause);
	        }

	        // Set all the fetched data
	        about.setPlaceRelatedImages(images);
	        about.setReviews(reviews);
	        about.setEvents(events);

	        // Local cuisines
	        List<AboutPlaceCuisineImageDTO> localCuisineDTOs = place.getLocalCuisines().stream().map(cuisine -> {
	            AboutPlaceCuisineImageDTO dto = new AboutPlaceCuisineImageDTO();
	            dto.setCuisineName(cuisine.getCuisineName());
	            dto.setImageUrl(images); // Using same images for simplicity
	            return dto;
	        }).collect(Collectors.toList());

	        about.setLocalCuisines(localCuisineDTOs);

	        // Nearby places
	        List<NearbyPlaceDTO> nearbyPlaceDTOs = getNearbyPlaces(place.getPlaceName(), 200.0).stream().map(p -> {
	            NearbyPlaceDTO dto = new NearbyPlaceDTO();
	            dto.setPlaceId(p.getPlaceId());
	            dto.setPlaceName(p.getPlaceName());
	            dto.setLatitude(p.getCoordinate().getLatitude());
	            dto.setLongitude(p.getCoordinate().getLongitude());
	            dto.setImageUrls(images);
	            return dto;
	        }).collect(Collectors.toList());

	        about.setNearByPlaces(nearbyPlaceDTOs);

	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        logger.error("Thread was interrupted while fetching place details for placeId {}: {}", placeId, e.getMessage());
	        throw new RuntimeException("Thread interrupted", e);
	    }

	    data.put("about", about);
	    aboutMapSection.put("data", data);
	    return aboutMapSection;
	}


	@Override
	public List<Place> getNearbyPlaces(String placeName, double radiusInKm) {
		// Step 1: Get the target place
		Place targetPlace = placeRepository.findByPlaceName(placeName)
				.orElseThrow(() -> new RuntimeException("Place not found: " + placeName));

		double targetLat = targetPlace.getCoordinate().getLatitude();
		double targetLon = targetPlace.getCoordinate().getLongitude();

		// Step 2: Get all places and calculate distance
		return placeRepository.findAll().stream().filter(p -> !p.getPlaceName().equalsIgnoreCase(placeName)) // exclude
																												// the
																												// current
																												// place
				.filter(p -> {
					double lat = p.getCoordinate().getLatitude();
					double lon = p.getCoordinate().getLongitude();
					return haversine(targetLat, targetLon, lat, lon) <= radiusInKm;
				}).collect(Collectors.toList());
	}

	private double haversine(double lat1, double lon1, double lat2, double lon2) {
		final double R = 6371; // Radius of the earth in KM
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}
	
	@Override
	public Map<String, Object> getPlaceId(String placeName) {
		Place place = placeRepository.findByPlaceName(placeName)
                .orElseThrow(() -> new PlaceNotFoundException(placeName)); // ✅ Exception here

        return getPlaceDetails(place.getPlaceId());
	}
   

}
