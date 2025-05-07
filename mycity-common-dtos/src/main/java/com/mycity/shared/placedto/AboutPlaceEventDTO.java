package com.mycity.shared.placedto;

import java.util.List;

import com.mycity.shared.mediadto.AboutPlaceImageDTO;

import lombok.Data;

@Data
public class AboutPlaceEventDTO {

	private String evetName;
	
	private List<AboutPlaceImageDTO> imageUrl;
}
