package com.mycity.shared.admindto;

import java.time.LocalDate;
import java.util.List;

import com.mycity.shared.mediadto.AboutPlaceImageDTO;

import lombok.Data;

@Data
public class AdminPlaceResponseDTO 
{
    private String placeName;
    private LocalDate currentDate;
    private List<AboutPlaceImageDTO> placeRelatedImages; 
    private String Location;
}
