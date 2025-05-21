package com.mycity.place.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycity.place.entity.PlaceDiscoveries;
import com.mycity.place.exception.MediaServiceUnavailableException;
import com.mycity.place.exception.TooManyPlacesException;
import com.mycity.place.repository.PlaceDiscoveryRepository;
import com.mycity.place.service.PlaceDiscoveriesInterface;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.mediadto.AboutPlaceImageDTO;
import com.mycity.shared.placedto.PlaceDiscoveriesDTO;
import com.mycity.shared.placedto.PlaceDiscoveriesResponeDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlaceDiscoveriesServiceimpl implements PlaceDiscoveriesInterface {

    @Autowired
    private PlaceDiscoveryRepository discoveryRepo;

    @Autowired
    private PlaceServiceInterface service;

    @Autowired
    private WebClientMediaService mediaService;

    @Override
    public String addPlaceToDiscoveries(PlaceDiscoveriesDTO dto) {
        Long no_of_places = discoveryRepo.count();
        log.info("Attempting to add a place to discoveries. Current count: {}", no_of_places);

        if (no_of_places >= 10) {
            log.warn("Too many places in discoveries: {}", no_of_places);
            throw new TooManyPlacesException("Cannot add more than top 10 places to discoveries");
        }

        if (dto.getPlaceName() == null || dto.getPlaceName().trim().isEmpty()) {
            log.error("Validation failed: Place Name is empty");
            throw new IllegalArgumentException("Place Name Cannot be Empty");
        } else if (dto.getPlaceCategory() == null || dto.getPlaceCategory().trim().isEmpty()) {
            log.error("Validation failed: Place Category is empty");
            throw new IllegalArgumentException("Place Category Cannot be Empty");
        }

        PlaceDiscoveries discovery = new PlaceDiscoveries();
        BeanUtils.copyProperties(dto, discovery);
        Long pId = discoveryRepo.save(discovery).getPlaceId();

        log.info("Place added to discoveries: ID={}, Name={}", pId, dto.getPlaceName());
        return "Place with Id " + pId + " added to top 10 discoveries";
    }
    @Override
    public List<PlaceDiscoveriesResponeDTO> getAllTopDisoveries() {
        List<PlaceDiscoveriesResponeDTO> places = new ArrayList<>();
        List<PlaceDiscoveries> discoveries = discoveryRepo.findAll();
        log.info("Fetching all top discoveries, total records: {}", discoveries.size());

        for (PlaceDiscoveries place : discoveries) {
            PlaceDiscoveriesResponeDTO discovery = new PlaceDiscoveriesResponeDTO();
            BeanUtils.copyProperties(place, discovery);

            try {
                CompletableFuture<List<AboutPlaceImageDTO>> imagesFuture = mediaService.getImagesForTop10Descoveries(place.getPlaceName());
                List<AboutPlaceImageDTO> images = imagesFuture.get();

                discovery.setPlaceRelatedImages(images);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Image fetch interrupted for {}", place.getPlaceName(), e);

                discovery.setPlaceRelatedImages(
                    List.of(new AboutPlaceImageDTO("Media service was interrupted while fetching images.")) // placeholder
                );

            } catch (ExecutionException e) {
                log.error("Media service unavailable while fetching images for {}", place.getPlaceName(), e);

                discovery.setPlaceRelatedImages(
                    List.of(new AboutPlaceImageDTO("Media service is currently unavailable.")) // placeholder
                );

            } catch (Exception e) {
                log.error("Unexpected error while processing place: {}", place.getPlaceName(), e);

                discovery.setPlaceRelatedImages(
                    List.of(new AboutPlaceImageDTO("Unexpected error while fetching images.")) // placeholder
                );
            }

            places.add(discovery);
        }

        return places;
    }


    @Override
    public PlaceResponseDTO getPlaceDetailsByName(String placeName) {
        log.warn("Method getPlaceDetailsByName is not implemented yet for placeName: {}", placeName);
        return null;
    }

    // Optional: remove or implement this properly if you need it in the future
//    @Override
//    public PlaceDTO getPlaceDetailsByName(String placeName) {
//        Long pId = service.getPlaceIdByName(placeName);
//        PlaceDTO place = service.getPlace(pId);
//        return place;
//    }
}
