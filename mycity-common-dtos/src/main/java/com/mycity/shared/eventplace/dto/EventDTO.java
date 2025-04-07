package com.mycity.shared.eventplace.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mycity.shared.place.dto.PlaceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private Long eventId;
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private PlaceDTO place;
}

