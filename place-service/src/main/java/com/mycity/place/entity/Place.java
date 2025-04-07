package com.mycity.place.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Place {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long placeId;

	private String placeName;

	@Column(columnDefinition = "TEXT")
	private String aboutPlace;

	@Column(columnDefinition = "TEXT")
	private String placeHistory;

	@ManyToOne
	@JoinColumn(name = "timezone_id")
	private TimeZone timeZone;

	private String placeCategory;

	private Double latitude;

	private Double longitude;
	
	private String  placeDistrict;

}
