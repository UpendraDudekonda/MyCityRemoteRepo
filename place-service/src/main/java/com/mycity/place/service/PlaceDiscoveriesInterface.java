package com.mycity.place.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.mycity.shared.placedto.PlaceDiscoveriesDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;
import com.mycity.shared.placedto.UserGalleryDTO;

public interface PlaceDiscoveriesInterface 
{
  String addPlaceToDiscoveries(PlaceDiscoveriesDTO dto);
  List<PlaceDiscoveriesDTO> getAllTopDisoveries();
  PlaceResponseDTO getPlaceDetailsByName(String placeName);
  
}
