package com.mycity.eventsplace.entity;


import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class EventHighlights {

    private LocalDate date;
    private LocalTime time;
    private String activityName;  // What is happening at this time
    
}
