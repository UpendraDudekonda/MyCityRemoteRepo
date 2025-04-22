package com.mycity.shared.locationdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoRequestDTO {
    private String sourceplace;
    private String destinationplace;
}
