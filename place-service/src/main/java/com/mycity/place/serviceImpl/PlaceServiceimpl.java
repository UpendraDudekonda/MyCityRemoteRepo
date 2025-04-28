package com.mycity.place.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.place.entity.Coordinate;
import com.mycity.place.entity.Place;
import com.mycity.place.entity.TimeZone;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.repository.TimeZoneRepository;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;
import com.mycity.shared.timezonedto.TimezoneDTO;

import com.mycity.shared.tripplannerdto.CoordinateDto;

@Service
public class PlaceServiceimpl implements PlaceServiceInterface {

    @Autowired
    private PlaceRepository placeRepo;

    @Autowired
    private TimeZoneRepository timeZoneRepo;
    
    @Autowired
    private WebClientMediaService mediaService;

    @Override
    public String addPlace(PlaceDTO dto) {
        // Input Validations
        if (dto.getPlaceName() == null || dto.getPlaceName().trim().isEmpty())
            throw new IllegalArgumentException("Enter Place Name");
        if (dto.getAboutPlace() == null)
            throw new IllegalArgumentException("Mention About the Place");
        if (dto.getPlaceHistory() == null)
            throw new IllegalArgumentException("Mention About the Place History");
        if (dto.getPlaceCategory() == null)
            throw new IllegalArgumentException("Mention the Category");
        if (dto.getPlaceDistrict() == null)
            throw new IllegalArgumentException("Mention the District of the Place");
        if (dto.getTimeZone() == null)
            throw new IllegalArgumentException("Mention the Place TimeZone details");
        if (dto.getCoordinate() == null)
            throw new IllegalArgumentException("Mention the Place Coordinates");
        if (dto.getRating() == null)
            throw new IllegalArgumentException("Mention the Rating");

        // Build Place
        CoordinateDto coordDTO = dto.getCoordinate();
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(coordDTO.getLatitude());
        coordinate.setLongitude(coordDTO.getLongitude());

        Place place = new Place();
        place.setPlaceName(dto.getPlaceName());
        place.setPlaceHistory(dto.getPlaceHistory());
        place.setAboutPlace(dto.getAboutPlace());
        place.setPlaceCategory(dto.getPlaceCategory());
        place.setPlaceDistrict(dto.getPlaceDistrict());
        place.setRating(dto.getRating());
        place.setCoordinate(coordinate);

        // Build TimeZone
        TimezoneDTO timezoneDTO = dto.getTimeZone();
        TimeZone timezone = new TimeZone();
        timezone.setOpeningTime(timezoneDTO.getOpeningTime());
        timezone.setClosingTime(timezoneDTO.getClosingTime());

        // Set bidirectional relationship
        timezone.setPlace(place);
        place.setTimeZone(timezone);

        // Save Place (cascade will save TimeZone too)
        String name = placeRepo.save(place).getPlaceName();
        return "Place with name " + name + " saved Successfully";
    }

    @Override
    public PlaceResponseDTO getPlace(Long placeId) {
        Place place = placeRepo.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Place Id.."));

        PlaceResponseDTO dto = new PlaceResponseDTO();
        dto.setPlaceId(place.getPlaceId());
        dto.setPlaceName(place.getPlaceName());
        dto.setAboutPlace(place.getAboutPlace());
        dto.setPlaceHistory(place.getPlaceHistory());
        dto.setPlaceCategory(place.getPlaceCategory());
        dto.setPlaceDistrict(place.getPlaceDistrict());
        dto.setRating(place.getRating());

        if (place.getCoordinate() != null) {
            dto.setLatitude(place.getCoordinate().getLatitude());
            dto.setLongitude(place.getCoordinate().getLongitude());
        }

        if (place.getTimeZone() != null) {
            dto.setOpeningTime(place.getTimeZone().getOpeningTime());
            dto.setClosingTime(place.getTimeZone().getClosingTime());
        }

        return dto;
    }


    @Override
    public String updatePlace(Long placeId, PlaceDTO dto) {
        Optional<Place> opt = placeRepo.findById(placeId);
        if (opt.isPresent()) {
            Place place = opt.get();
            place.setPlaceName(dto.getPlaceName());
            place.setAboutPlace(dto.getAboutPlace());
            place.setPlaceHistory(dto.getPlaceHistory());
            place.setPlaceDistrict(dto.getPlaceDistrict());
            place.setPlaceCategory(dto.getPlaceCategory());
            place.setRating(dto.getRating());

            // Update coordinates
            CoordinateDto coordDTO = dto.getCoordinate();
            Coordinate coordinate = new Coordinate();
            coordinate.setLatitude(coordDTO.getLatitude());
            coordinate.setLongitude(coordDTO.getLongitude());
            place.setCoordinate(coordinate);

            // Update timezone (create new if null)
            TimezoneDTO timezoneDTO = dto.getTimeZone();
            TimeZone timezone = place.getTimeZone();
            if (timezone == null) {
                timezone = new TimeZone();
                timezone.setPlace(place);
            }
            timezone.setOpeningTime(timezoneDTO.getOpeningTime());
            timezone.setClosingTime(timezoneDTO.getClosingTime());

            place.setTimeZone(timezone);

            Long idVal = placeRepo.save(place).getPlaceId();
            return "Place With Id ::" + idVal + " is Updated";
        } else {
            throw new IllegalArgumentException("Invalid Place Id..");
        }
    }

    @Override
    public String deletePlace(Long placeId) {
        placeRepo.deleteById(placeId);
        return "Place With Id ::" + placeId + " Deleted Successfully.";
    }

    @Override
    public Place savePlace(Place place) {
        return placeRepo.save(place);
    }

    @Override
    public Place getPlaceById(Long id) {
        return placeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Place Id.."));
    }
    

    @Override
    public List<PlaceResponseDTO> getAllPlaces() {
        List<Place> places = placeRepo.findAll();
        return places.stream().map(this::convertToDTO).collect(Collectors.toList());
    }



    @Override
    public List<PlaceCategoryDTO> getAllDistinctCategories() {
        return placeRepo.findPlaceIdsAndCategories();
    }

    
    
    
    private PlaceResponseDTO convertToDTO(Place place) {
        PlaceResponseDTO dto = new PlaceResponseDTO();
        dto.setPlaceId(place.getPlaceId());
        dto.setPlaceName(place.getPlaceName());
        dto.setAboutPlace(place.getAboutPlace());
        dto.setPlaceHistory(place.getPlaceHistory());
        dto.setPlaceCategory(place.getPlaceCategory());
        dto.setPlaceDistrict(place.getPlaceDistrict());
        dto.setRating(place.getRating());

        if (place.getCoordinate() != null) {
            dto.setLatitude(place.getCoordinate().getLatitude());
            dto.setLongitude(place.getCoordinate().getLongitude());
        }

        if (place.getTimeZone() != null) {
            dto.setOpeningTime(place.getTimeZone().getOpeningTime());
            dto.setClosingTime(place.getTimeZone().getClosingTime());
        }

        return dto;
    }

    @Override
    public String addPlace(PlaceDTO dto, List<MultipartFile> images) {

        // === Validation ===
        if (dto.getPlaceName() == null || dto.getPlaceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Enter Place Name");
        } else if (dto.getAboutPlace() == null) {
            throw new IllegalArgumentException("Mention About the Place");
        } else if (dto.getPlaceHistory() == null) {
            throw new IllegalArgumentException("Mention About the Place History");
        } else if (dto.getPlaceCategory() == null) {
            throw new IllegalArgumentException("Mention the Category");
        } else if (dto.getPlaceDistrict() == null) {
            throw new IllegalArgumentException("Mention the District of the Place");
        } else if (dto.getTimeZone() == null) {
            throw new IllegalArgumentException("Mention the place TimeZone");
        } else if (dto.getCoordinate() == null) {
            throw new IllegalArgumentException("Mention the Place Coordinates");
        } else if (dto.getRating() == null) {
            throw new IllegalArgumentException("Mention the Rating");
        }

        // === TimeZone Entity ===
        TimezoneDTO timezoneDTO = dto.getTimeZone();
        TimeZone timezone = new TimeZone();
        timezone.setTimeZoneId(timezoneDTO.getTimezoneId());
        timezone.setOpeningTime(timezoneDTO.getOpeningTime());
        timezone.setClosingTime(timezoneDTO.getClosingTime());

        // === Coordinate Entity ===
        CoordinateDto coordDTO = dto.getCoordinate();
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(coordDTO.getLatitude());
        coordinate.setLongitude(coordDTO.getLongitude());

        // === Place Entity ===
        Place place = new Place();
        place.setPlaceName(dto.getPlaceName());
        place.setPlaceHistory(dto.getPlaceHistory());
        place.setAboutPlace(dto.getAboutPlace());
        place.setPlaceCategory(dto.getPlaceCategory());
        place.setPlaceDistrict(dto.getPlaceDistrict());
        place.setRating(dto.getRating());
        place.setTimeZone(timezone);
        place.setCoordinate(coordinate);

        // Set bidirectional relationship
        timezone.setPlace(place);
        place.setTimeZone(timezone);

        // === Save to DB ===
        Place savedPlace = placeRepo.save(place);
        long placeId = savedPlace.getPlaceId();

        // === Upload Images ===
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                mediaService.uploadImageForPlace(
                    image,
                    savedPlace.getPlaceId(),
                    savedPlace.getPlaceName(),
                    savedPlace.getPlaceCategory(),
                    dto.getImageName()
                );
            }
        }

        return "Place with name " + savedPlace.getPlaceName() + " saved Successfully, and image uploads initiated.";
    }


    @Override
    public Long getPlaceIdByName(String placeName) {
        return placeRepo.findPlaceIdByPlaceName(placeName);
    }

}