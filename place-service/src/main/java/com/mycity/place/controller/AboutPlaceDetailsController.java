package com.mycity.place.controller;

import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.place.entity.Place;
import com.mycity.place.service.PlaceDetailService;

@RestController
@RequestMapping("/place")
public class AboutPlaceDetailsController {

    @Autowired
    private PlaceDetailService placeDetailsService;

    @GetMapping("/about/{placeId}")
    public Map<String, Object> getPlaceDetails(@PathVariable Long placeId) {
        return placeDetailsService.getPlaceDetails(placeId);
    }
    
    @GetMapping("/places/nearby")
    public ResponseEntity<List<Place>> getNearbyPlaces(
            @RequestParam String placeName,
            @RequestParam double radiusKm) {

        List<Place> nearbyPlaces = placeDetailsService.getNearbyPlaces(placeName, radiusKm);
        return ResponseEntity.ok(nearbyPlaces);
    }

}