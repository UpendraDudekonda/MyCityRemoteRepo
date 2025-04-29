package com.mycity.place.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.place.entity.Coordinate;
import com.mycity.place.entity.Place;
import com.mycity.place.entity.TimeZone;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.categorydto.CategoryDTO;
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;
import com.mycity.shared.timezonedto.TimezoneDTO;

import jakarta.transaction.Transactional;

@Service
public class PlaceServiceImpl implements PlaceServiceInterface {

    @Autowired
    private PlaceRepository placeRepo;

    @Autowired
    private WebClientMediaService mediaService;

    @Autowired
    private WebClientCategoryService categoryService;

    @Override
    @Transactional
    public String addPlace(PlaceDTO dto, List<MultipartFile> images) {
        // Validate incoming DTO
        validatePlaceDTO(dto);

        // Build Coordinate entity
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(dto.getCoordinate().getLatitude());
        coordinate.setLongitude(dto.getCoordinate().getLongitude());

        // Build Place entity
        Place place = new Place();
        place.setPlaceName(dto.getPlaceName().trim());
        place.setPlaceHistory(dto.getPlaceHistory());
        place.setAboutPlace(dto.getAboutPlace());
        place.setPlaceDistrict(dto.getPlaceDistrict());
        place.setRating(dto.getRating());
        place.setCoordinate(coordinate);
        place.setCategoryName(dto.getCategoryName());

        // Set PostedOn date
        if (dto.getPostedOn() != null) {
            place.setPostedOn(dto.getPostedOn());
        } else {
            place.setPostedOn(LocalDate.now());  // fallback to current date if not provided
        }

        // Save Place first to get the generated placeId
        Place savedPlace = placeRepo.save(place);
        placeRepo.flush();  // Force insert to get generated ID immediately

        // Build CategoryDTO using saved Place info
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(savedPlace.getCategoryName());
        categoryDTO.setPlaceName(savedPlace.getPlaceName());
        categoryDTO.setPlaceId(savedPlace.getPlaceId());
        categoryDTO.setDescription(dto.getPlaceCategoryDescription());

        // Create/save Category
        CategoryDTO createdCategory = categoryService.saveCategory(categoryDTO);

        // Update Place with Category ID and Category Name
        savedPlace.setCategoryId(createdCategory.getCategoryId());
        savedPlace.setCategoryName(createdCategory.getName());

        // Save updated Place (with categoryId)
        placeRepo.save(savedPlace);

        // Handle Timezone if provided
        TimezoneDTO timezoneDTO = dto.getTimeZone();
        if (timezoneDTO != null) {
            TimeZone timezone = new TimeZone();
            timezone.setOpeningTime(timezoneDTO.getOpeningTime());
            timezone.setClosingTime(timezoneDTO.getClosingTime());
            timezone.setPlace(savedPlace);  // Bidirectional

            savedPlace.setTimeZone(timezone);

            placeRepo.save(savedPlace);
        }

        // Handle Image Uploads if images are provided
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                mediaService.uploadImageForPlace(
                    image,
                    savedPlace.getPlaceId(),
                    savedPlace.getPlaceName(),
                    savedPlace.getCategoryName(),
                    dto.getImageName()
                );
            }
        }

        return "Place with name '" + savedPlace.getPlaceName() + "' saved successfully, and image uploads initiated.";
    }




    @Override
    @Transactional
    public PlaceResponseDTO getPlace(Long placeId) {
        Place place = placeRepo.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Place Id.."));

        return convertToDTO(place);
    }

    @Override
    @Transactional
    public String updatePlace(Long placeId, PlaceDTO dto) {
        validatePlaceDTO(dto);

        Place place = placeRepo.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Place Id.."));

        // Update Place Fields
        place.setPlaceName(dto.getPlaceName().trim());
        place.setAboutPlace(dto.getAboutPlace());
        place.setPlaceHistory(dto.getPlaceHistory());
        place.setPlaceDistrict(dto.getPlaceDistrict());
        place.setRating(dto.getRating());

        // Update Category
        CategoryDTO category = getCategory(dto);
        place.setCategoryId(category.getCategoryId());
        place.setCategoryName(category.getName());

        // Update Coordinates
        if (dto.getCoordinate() != null) {
            Coordinate coordinate = place.getCoordinate();
            if (coordinate == null) {
                coordinate = new Coordinate();
            }
            coordinate.setLatitude(dto.getCoordinate().getLatitude());
            coordinate.setLongitude(dto.getCoordinate().getLongitude());
            place.setCoordinate(coordinate);
        }

        // Update TimeZone
        if (dto.getTimeZone() != null) {
            TimeZone timezone = place.getTimeZone();
            if (timezone == null) {
                timezone = new TimeZone();
                timezone.setPlace(place);
            }
            timezone.setOpeningTime(dto.getTimeZone().getOpeningTime());
            timezone.setClosingTime(dto.getTimeZone().getClosingTime());
            place.setTimeZone(timezone);
        }

        // Save Updated Place
        placeRepo.save(place);

        return "Place with ID " + placeId + " updated successfully.";
    }

    @Override
    @Transactional
    public String deletePlace(Long placeId) {
        placeRepo.deleteById(placeId);
        return "Place With Id ::" + placeId + " Deleted Successfully.";
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

    @Override
    public Long getPlaceIdByName(String placeName) {
        return placeRepo.findPlaceIdByPlaceName(placeName);
    }

    private CategoryDTO getCategory(PlaceDTO dto) {
        CategoryDTO category = categoryService.getCategoryByName(dto.getCategoryName());
        if (category == null) {
            category = categoryService.createCategory(dto.getCategoryName(), dto.getPlaceCategoryDescription());
        }
        return category;
    }

    private PlaceResponseDTO convertToDTO(Place place) {
        PlaceResponseDTO dto = new PlaceResponseDTO();
        dto.setPlaceId(place.getPlaceId());
        dto.setPlaceName(place.getPlaceName());
        dto.setAboutPlace(place.getAboutPlace());
        dto.setPlaceHistory(place.getPlaceHistory());
        dto.setCategoryId(place.getCategoryId());
        dto.setCategoryName(place.getCategoryName());
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

    private void validatePlaceDTO(PlaceDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Place data must not be null");
        }
        if (isNullOrEmpty(dto.getPlaceName())) {
            throw new IllegalArgumentException("Enter Place Name");
        }
        if (isNullOrEmpty(dto.getAboutPlace())) {
            throw new IllegalArgumentException("Mention About the Place");
        }
        if (isNullOrEmpty(dto.getPlaceHistory())) {
            throw new IllegalArgumentException("Mention About the Place History");
        }
        if (isNullOrEmpty(dto.getPlaceDistrict())) {
            throw new IllegalArgumentException("Mention the District of the Place");
        }
        if (dto.getCoordinate() == null) {
            throw new IllegalArgumentException("Mention the Place Coordinates");
        }
        if (dto.getTimeZone() == null) {
            throw new IllegalArgumentException("Mention the Place TimeZone details");
        }
        if (dto.getRating() == null) {
            throw new IllegalArgumentException("Mention the Rating");
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
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
	public String addPlace(PlaceDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}
    
}