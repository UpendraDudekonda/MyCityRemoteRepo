package com.mycity.place.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.mycity.place.entity.Coordinate;
import com.mycity.place.entity.Place;
import com.mycity.place.entity.TimeZone;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.shared.placedto.PlaceCategoryDTO;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;

    private Place place;

    @BeforeEach
    void setUp() {
        // Prepare the place entity with necessary data
        place = new Place();
        place.setPlaceName("Green Park");
        place.setPlaceHistory("Historic Park");
        place.setCategoryId(1L);
        place.setCategoryName("Nature");
        place.setRating(4.5);
        place.setPlaceDistrict("Central District");
        place.setCoordinate(new Coordinate(28.6139, 77.2090)); // Sample coordinates
        place.setTimeZone(new TimeZone(LocalTime.of(9, 0), LocalTime.of(18, 0)));
        place.setPostedOn(LocalDate.now());

        // Save the place to the repository
        placeRepository.save(place);
    }

    @Test
    void testFindPlaceIdsAndCategories() {
        // Ensure the data is saved correctly in the repository and return the expected results
        List<PlaceCategoryDTO> results = placeRepository.findPlaceIdsAndCategories();

        // Debug output to verify the actual data being returned
        results.forEach(dto -> System.out.println("Place Name: " + dto.getPlaceName() + ", Category: " + dto.getCategoryName()));

        // Assert that results are not empty and check the place name and category
        assertThat(results).isNotEmpty();
        PlaceCategoryDTO dto = results.get(0);

        // Check that the expected place name and category match
        assertThat(dto.getPlaceName()).isEqualTo("Rajahmundry");
        assertThat(dto.getCategoryName()).isEqualTo("Cultural");
    }

    @Test
    void testFindPlaceIdByPlaceName() {
        // Ensure that findPlaceIdByPlaceName returns the correct ID for Green Park
        Long id = placeRepository.findPlaceIdByPlaceName("Green Park");
        assertThat(id).isEqualTo(place.getPlaceId());
    }

    @Test
    void testFindPlaceIdByPlaceNameIgnoreCase() {
        // Ensure case-insensitivity works for place name lookup
        Long id = placeRepository.findPlaceIdByPlaceNameIgnoreCase("Green Park");
        System.out.println("Place Id "+id);
        assertThat(id).isEqualTo(place.getPlaceId());
    }

    @Test
    void testFindByPlaceName() {
        // Test if findByPlaceName returns the correct place
        Optional<Place> result = placeRepository.findByPlaceName("Green Park");
        assertThat(result).isPresent();
        assertThat(result.get().getPlaceName()).isEqualTo("Green Park");
    }

    @Test
    void testFindByCategoryName() {
        // Test if places can be found by category name
        List<Place> results = placeRepository.findByCategoryName("Nature");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getCategoryName()).isEqualTo("Nature");
    }

    @Test
    void testFindByCategoryName_ignoreCase() {
        // Test case-insensitivity in category name lookup
        List<Place> results = placeRepository.findByCategoryName("nature");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getCategoryName()).isEqualTo("Nature");
    }
}


