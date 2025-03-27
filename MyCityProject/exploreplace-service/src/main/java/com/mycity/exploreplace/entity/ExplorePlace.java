package com.mycity.exploreplace.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ExplorePlace {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long placeId;
	    private String placeName;
	    private String description;
	    private String category;
	    private Double latitude;
	    private Double longitude;
	    private String address;
	    private List<String> imageUrls;
	    private Double rating;
	    private String phoneNumber;
	    private String LocalTime;
	    private List<String> tags;
	    private List<Long> reviewIds;
	


}
