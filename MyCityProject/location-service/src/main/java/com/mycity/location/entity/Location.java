package com.mycity.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "locations")
public class Location {


	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String placeName;  // Name of the location
	    private Double latitude;
	    private Double longitude;
	    private String address;
	    private String city;
	    private String state;
	    private String country;
	    private String postalCode;

	    public Location() {}
	
	    

}
