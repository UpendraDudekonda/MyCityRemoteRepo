package com.mycity.shared.mediadto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMainImageDTO {

    private Long imageId;
    private String imageUrl;
    private String eventName;
  
    private Long eventId;
}

