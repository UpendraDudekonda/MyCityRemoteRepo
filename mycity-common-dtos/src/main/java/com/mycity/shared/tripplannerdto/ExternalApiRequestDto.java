package com.mycity.shared.tripplannerdto;


import lombok.AllArgsConstructor;
import lombok.Builder; // Lombok annotation for builder pattern
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder // Useful for constructing the request object
@AllArgsConstructor
@NoArgsConstructor
public class ExternalApiRequestDto {
	
    private String departureLocation; // Maybe they use different names
    private String arrivalLocation;
    private LocalDate tripStart; // Date format might be different, need to adjust
    private LocalDate tripEnd;
    private String mode; // Match their mode enum/strings
    private String preferences; // Match their format
    // ... other fields required by the external API
}

