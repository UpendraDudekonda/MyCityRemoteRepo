package com.mycity.place.entity;

import java.time.LocalTime;

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
public class TimeZone {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long timeZoneId;
    
	private LocalTime openingTime;
	
	private LocalTime closingTime;
}
