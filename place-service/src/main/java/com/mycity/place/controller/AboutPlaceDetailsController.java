package com.mycity.place.controller;

import com.mycity.place.entity.Place;
import com.mycity.place.exception.PlaceNotFoundException;
import com.mycity.place.service.PlaceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/place")
public class AboutPlaceDetailsController {

    @Autowired
    private PlaceDetailService placeDetailsService;

    @GetMapping("/about/{placeName}")
    public ResponseEntity<Map<String, Object>> getPlaceDetails(@PathVariable String placeName) {
        Map<String, Object> details = placeDetailsService.getPlaceId(placeName);
        return ResponseEntity.ok(details); 
    }

    // ✅ Still uses 200 OK or 404 Not Found for nearby places
    @GetMapping("/places/nearby")
    public ResponseEntity<List<Place>> getNearbyPlaces(
            @RequestParam String placeName,
            @RequestParam double radiusKm) {

        try {
            List<Place> nearbyPlaces = placeDetailsService.getNearbyPlaces(placeName, radiusKm);

            if (nearbyPlaces == null || nearbyPlaces.isEmpty()) {
                throw new PlaceNotFoundException("Nearby places not found for: " + placeName);
            }

            return ResponseEntity.ok(nearbyPlaces);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get nearby places", ex); // handled globally
        }
    }
}
