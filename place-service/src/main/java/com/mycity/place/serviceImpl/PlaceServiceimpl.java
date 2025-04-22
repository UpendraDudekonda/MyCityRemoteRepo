package com.mycity.place.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.place.entity.Coordinate;
import com.mycity.place.entity.Place;
import com.mycity.place.entity.TimeZone;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.timezonedto.TimezoneDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;

@Service
public class PlaceServiceimpl implements PlaceServiceInterface {

    @Autowired
    private PlaceRepository Placerepo;
    
    @Autowired
	private WebClientMediaService mediaService;

    @Override
    public String addPlace(PlaceDTO dto) {
        if (dto.getPlaceName() == null || dto.getPlaceName().trim().isEmpty())
            throw new IllegalArgumentException("Enter Place Name");
        else if (dto.getAboutPlace() == null)
            throw new IllegalArgumentException("Mention About the Place");
        else if (dto.getPlaceHistory() == null)
            throw new IllegalArgumentException("Mention About the Place History");
        else if (dto.getPlaceCategory() == null)
            throw new IllegalArgumentException("Mention the Category");
        else if (dto.getPlaceDistrict() == null)
            throw new IllegalArgumentException("Mention the District of the Place");
        else if (dto.getTimeZone() == null)
            throw new IllegalArgumentException("Mention the place TimeZone");
        else if (dto.getCoordinate() == null)
            throw new IllegalArgumentException("Mention the Place Coordinates");

        TimezoneDTO timezoneDTO = dto.getTimeZone();
        TimeZone timezone = new TimeZone();
        timezone.setTimeZoneId(timezoneDTO.getTimezoneId());
        timezone.setOpeningTime(timezoneDTO.getOpeningTime());
        timezone.setClosingTime(timezoneDTO.getClosingTime());
        

        CoordinateDTO coordDTO = dto.getCoordinate();
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(coordDTO.getLatitude());
        coordinate.setLongitude(coordDTO.getLongitude());

        Place place = new Place();
        place.setPlaceName(dto.getPlaceName());
        place.setPlaceHistory(dto.getPlaceHistory());
        place.setAboutPlace(dto.getAboutPlace());
        place.setPlaceCategory(dto.getPlaceCategory());
        place.setPlaceDistrict(dto.getPlaceDistrict());
        place.setTimeZone(timezone);
        place.setCoordinate(coordinate);

        timezone.setPlace(place);

        String name = Placerepo.save(place).getPlaceName();
        return "Place with name " + name + " saved Successfully";
    }

    @Override
    public Place getPlace(Long placeId) {
        Optional<Place> opt = Placerepo.findById(placeId);
        if (opt.isPresent())
            return opt.get();
        else
            throw new IllegalArgumentException("Invalid Place Id..");
    }

    @Override
    public String updatePlace(Long placeId, PlaceDTO dto) {
        Optional<Place> opt = Placerepo.findById(placeId);
        if (opt.isPresent()) {
            Place place = opt.get();
            place.setPlaceName(dto.getPlaceName());
            place.setAboutPlace(dto.getAboutPlace());
            place.setPlaceHistory(dto.getPlaceHistory());
            place.setPlaceDistrict(dto.getPlaceDistrict());
            place.setPlaceCategory(dto.getPlaceCategory());

            CoordinateDTO coordDTO = dto.getCoordinate();
            Coordinate coordinate = new Coordinate();
            coordinate.setLatitude(coordDTO.getLatitude());
            coordinate.setLongitude(coordDTO.getLongitude());
            place.setCoordinate(coordinate);

            TimezoneDTO timezoneDTO = dto.getTimeZone();
            TimeZone time = new TimeZone();
            time.setTimeZoneId(timezoneDTO.getTimezoneId());
            time.setOpeningTime(timezoneDTO.getOpeningTime());
            time.setClosingTime(timezoneDTO.getClosingTime());
            
            place.setTimeZone(time);


            Long idVal = Placerepo.save(place).getPlaceId();
            return "Place With Id ::" + idVal + " is Updated";
        } else {
            throw new IllegalArgumentException("Invalid Place Id..");
        }
    }

    @Override
    public String deletePlace(Long placeId) {
        Placerepo.deleteById(placeId);
        return "Place With Id ::" + placeId + " Deleted Successfully.";
    }

    public String addPlace(PlaceDTO dto, List<MultipartFile> images) {
        if (dto.getPlaceName() == null || dto.getPlaceName().trim().isEmpty())
            throw new IllegalArgumentException("Enter Place Name");
        else if (dto.getAboutPlace() == null)
            throw new IllegalArgumentException("Mention About the Place");
        else if (dto.getPlaceHistory() == null)
            throw new IllegalArgumentException("Mention About the Place History");
        else if (dto.getPlaceCategory() == null)
            throw new IllegalArgumentException("Mention the Category");
        else if (dto.getPlaceDistrict() == null)
            throw new IllegalArgumentException("Mention the District of the Place");
        else if (dto.getTimeZone() == null)
            throw new IllegalArgumentException("Mention the place TimeZone");
        else if (dto.getCoordinate() == null)
            throw new IllegalArgumentException("Mention the Place Coordinates");

        TimezoneDTO timezoneDTO = dto.getTimeZone();
        TimeZone timezone = new TimeZone();
        timezone.setTimeZoneId(timezoneDTO.getTimezoneId());
        timezone.setOpeningTime(timezoneDTO.getOpeningTime());
        timezone.setClosingTime(timezoneDTO.getClosingTime());

        CoordinateDTO coordDTO = dto.getCoordinate();
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(coordDTO.getLatitude());
        coordinate.setLongitude(coordDTO.getLongitude());

        Place place = new Place();
        place.setPlaceName(dto.getPlaceName());
        place.setPlaceHistory(dto.getPlaceHistory());
        place.setAboutPlace(dto.getAboutPlace());
        place.setPlaceCategory(dto.getPlaceCategory());
        place.setPlaceDistrict(dto.getPlaceDistrict());
        place.setTimeZone(timezone);
        place.setCoordinate(coordinate);
        // Do not set photoUrls here if you are uploading images

        timezone.setPlace(place);

        Place savedPlace = Placerepo.save(place);
        long placeId = savedPlace.getPlaceId(); // Assuming your Place entity has a placeId

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
               mediaService.uploadImageForPlace(image,savedPlace.getPlaceId(), savedPlace.getPlaceName(), savedPlace.getPlaceCategory(),dto.getImageName()); // Assuming PlaceCategory has getId()
            }
        }

        return "Place with name " + savedPlace.getPlaceName() + " saved Successfully, and image uploads initiated.";
    }
}
