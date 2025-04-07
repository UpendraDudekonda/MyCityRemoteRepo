package com.mycity.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.booking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}
