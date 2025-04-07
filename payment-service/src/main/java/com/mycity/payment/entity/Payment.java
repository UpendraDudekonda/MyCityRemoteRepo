package com.mycity.payment.entity;

import java.time.LocalDateTime;

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
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@Column(name = "user_id")
	private Long userId; // ID of the user making the payment (from the User service)

	@Column(name = "booking_id")
	private Long bookingId; // Associated booking ID (if applicable)

	private Double amount; // Payment amount

	private String currency; // Currency type (USD, INR, etc.)

	private String paymentMethod; // Credit Card, UPI, Net Banking, etc.

	private String transactionId; // Unique transaction ID from the payment gateway

	private String paymentStatus; // SUCCESS, PENDING, FAILED

	private LocalDateTime paymentDate; // Date and time of payment

	private String failureReason; // Reason for failure (if any)

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
