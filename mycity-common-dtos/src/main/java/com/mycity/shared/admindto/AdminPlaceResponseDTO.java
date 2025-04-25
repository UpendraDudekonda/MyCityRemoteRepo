package com.mycity.shared.admindto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AdminPlaceResponseDTO 
{
    private String placeName;
    private LocalDate currentDate;
    private String ImageUrl;
    private LocalDate postedOn;
    
    
}
