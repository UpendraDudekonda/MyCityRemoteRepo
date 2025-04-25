package com.mycity.place.service;

import java.util.List;

import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceDiscoveriesDTO;

public interface PlaceDiscoveriesInterface 
{
  String addPlaceToDiscoveries(PlaceDiscoveriesDTO dto);
  List<PlaceDiscoveriesDTO> getAllTopDisoveries();
  PlaceDTO getPlaceDetailsByName(String placeName);
  
}
