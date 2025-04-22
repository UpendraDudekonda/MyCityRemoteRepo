package com.mycity.shared.mediadto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSubImagesDTO {

    private Long imageId;
    private List<String> imageUrls;
    private String eventName;
  
    private Long eventId;
}

