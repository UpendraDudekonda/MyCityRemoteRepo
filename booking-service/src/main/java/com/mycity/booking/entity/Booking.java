package com.mycity.booking.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookingId;

	@Column(name = "user_id")
	private Long userId; // The user who made the booking (from User service)

	@Column(name = "place_id")
	private Long placeId; // The place being booked (from Place service)

	private String bookingStatus; // CONFIRMED, PENDING, CANCELED

	private LocalDate bookingDate; // Date when the booking was made

	private LocalTime bookingTime; // Time of the booking

}
