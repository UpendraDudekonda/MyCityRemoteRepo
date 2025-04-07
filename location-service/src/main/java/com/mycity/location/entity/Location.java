package com.mycity.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long locationId;

	@Column(name = "place_id")
	private Long placeId; // Storing the ID of the place from the other service

	private Double latitude;

	private Double longitude;

	private String address;

	private String city;

	private String state;

	private String country;
	
	private String postalCode;

}
