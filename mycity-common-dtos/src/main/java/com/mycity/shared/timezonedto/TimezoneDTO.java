package com.mycity.shared.timezonedto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimezoneDTO 
{	
    private String name;
    private LocalTime OpeningTime;
    private LocalTime ClosingTime;
    
    
}

