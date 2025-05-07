package com.mycity.media.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuisine_images")  // Make sure this matches the actual DB table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuisineImages {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long imageId;
	
	private String imageUrl;
	private long placeId;
	private long cuisineId;
	private String placeName;
	private String category;
	private String cuisineName;
}

