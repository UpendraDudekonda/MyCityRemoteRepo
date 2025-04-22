package com.mycity.place.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Place {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long placeId;
	
    
	@NonNull
	private String placeName;

	@NonNull
	@Column(columnDefinition = "TEXT")
	private String aboutPlace;
 
	@NonNull
	@Column(columnDefinition = "TEXT")
	private String placeHistory;

	@OneToOne(targetEntity = TimeZone.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name="place_id",referencedColumnName = "placeId")
	private TimeZone timeZone;
	
    private Double rating;

	private String placeCategory;
	
	private String  placeDistrict;

	private Coordinate coordinate;
	
	private List<String> photoUrls;
	

}
