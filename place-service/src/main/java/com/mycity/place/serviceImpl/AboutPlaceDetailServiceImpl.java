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
		about.setCloingTime(place.getTimeZone().getClosingTime()); // Make sure DTO has the correct spelling
		about.setRating(place.getRating());
		about.setLatitude(place.getCoordinate().getLatitude());
		about.setLongitude(place.getCoordinate().getLongitude());
	

		try {
			// Fetching data from different services asynchronously
			CompletableFuture<List<AboutPlaceImageDTO>> imagesFuture = mediaService.getImagesForPlace(placeId);
			CompletableFuture<List<ReviewDTO>> reviewsFuture = reviewService.fetchReviews(placeId);
			CompletableFuture<List<AboutPlaceEventDTO>>  eventFuture= eventService.fetchEvents(placeId);
			// Blocking to get the results
			List<AboutPlaceImageDTO> images = imagesFuture.get();
			List<ReviewDTO> reviews = reviewsFuture.get();
			List<AboutPlaceEventDTO> events = eventFuture.get();

			// Set images and reviews
			about.setPlaceRelatedImages(images);
			about.setReviews(reviews);
            about.setEvents(events);
			
			// ✅ Fetch and map local cuisines directly from the Place entity
			List<LocalCuisine> localCuisines = place.getLocalCuisines();

			List<AboutPlaceCuisineImageDTO> localCuisineDTOs = new ArrayList<>();
			for (LocalCuisine cuisine : localCuisines) {
				AboutPlaceCuisineImageDTO dto = new AboutPlaceCuisineImageDTO();
				dto.setCuisineName(cuisine.getCuisineName()); // assuming getter exists
				dto.setImageUrl(images); // assuming getter exists
				localCuisineDTOs.add(dto);
			}

			about.setLocalCuisines(localCuisineDTOs);

			// ✅ Fetch nearby places and convert to DTOs
			List<Place> nearbyPlaces = getNearbyPlaces(place.getPlaceName(), 500.0); // radius in KM

			List<NearbyPlaceDTO> nearbyPlaceDTOs = new ArrayList<>();
			for (Place p : nearbyPlaces) {
				NearbyPlaceDTO dto = new NearbyPlaceDTO();
				dto.setPlaceId(p.getPlaceId());
				dto.setPlaceName(p.getPlaceName());
				dto.setLatitude(p.getCoordinate().getLatitude());
				dto.setLongitude(p.getCoordinate().getLongitude());

				dto.setImageUrls(images);

				nearbyPlaceDTOs.add(dto);
			}

			about.setNearByPlaces(nearbyPlaceDTOs);

		} catch (InterruptedException | ExecutionException e) {
			logger.error("Error fetching data from services: {}", e.getMessage());

			// Fallback values
			about.setPlaceRelatedImages(new ArrayList<>());
			about.setReviews(new ArrayList<>());
			about.setNearByPlaces(new ArrayList<>());
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

   

}
