package com.mycity.place.entity;

import java.time.LocalTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class TimeZone
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long timeZoneId;
	
	@OneToOne(targetEntity = Place.class,cascade = CascadeType.ALL)
	@JoinColumn(name="place_id",referencedColumnName = "placeId")
	private Place place;
	
	@NonNull
	private LocalTime openingTime;
	
	@NonNull
	private LocalTime closingTime;
	
	
}
