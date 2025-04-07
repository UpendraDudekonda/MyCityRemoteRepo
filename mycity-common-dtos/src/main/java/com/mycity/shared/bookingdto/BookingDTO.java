package com.mycity.shared.bookingdto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long bookingId;
    private Long userId;
    private Long placeId;
    private String bookingStatus;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
}

