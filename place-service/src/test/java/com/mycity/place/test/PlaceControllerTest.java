package com.mycity.place.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.place.serviceImpl.WebClientMediaService;
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;
import com.mycity.shared.timezonedto.TimezoneDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaceServiceInterface placeService; 

    @MockBean
    private PlaceRepository repo;
    
    @Mock
    private WebClientMediaService mediaService; 
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private PlaceResponseDTO mockPlaceResponse;
    
   
    @BeforeEach
    void setUp() {
        mockPlaceResponse = new PlaceResponseDTO();
        mockPlaceResponse.setPlaceId(1L);
        mockPlaceResponse.setPlaceName("Taj Mahal");
        mockPlaceResponse.setAboutPlace("A historical monument.");
        mockPlaceResponse.setPlaceHistory("Built by Shah Jahan.");
        mockPlaceResponse.setCategoryId(2L);
        mockPlaceResponse.setCategoryName("Historical");
        mockPlaceResponse.setPlaceDistrict("Agra");
        mockPlaceResponse.setRating(4.8);
        mockPlaceResponse.setLatitude(27.1751);
        mockPlaceResponse.setLongitude(78.0421);
        mockPlaceResponse.setOpeningTime(LocalTime.parse("06:00"));
        mockPlaceResponse.setClosingTime(LocalTime.parse("18:00"));
    }
    
    @Test
    void testAddPlaceDetailsSuccess() throws Exception {

        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setPlaceId(1L);
        placeDTO.setPlaceName("Test Fort");
        placeDTO.setAboutPlace("Historic place");
        placeDTO.setPlaceHistory("Built in 1800s");
        placeDTO.setPlaceDistrict("Sample District");
        placeDTO.setRating(4.8);
        placeDTO.setCategoryName("Historical");
        placeDTO.setImageName("fort.jpg");
        placeDTO.setPlaceCategoryDescription("Old ruins");
        placeDTO.setPostedOn(LocalDate.now());

        CoordinateDTO coordinate = new CoordinateDTO(12.3456, 78.9123);
        placeDTO.setCoordinate(coordinate);

        TimezoneDTO timezone = new TimezoneDTO();
        timezone.setName("NewYork");
        timezone.setOpeningTime(LocalTime.parse("09:00"));
        timezone.setClosingTime(LocalTime.parse("17:00"));
        placeDTO.setTimeZone(timezone);

        // Convert DTO to JSON
        String json = objectMapper.writeValueAsString(placeDTO);

        MockMultipartFile placePart = new MockMultipartFile("placeDto", "", "application/json", json.getBytes());
        MockMultipartFile imagePart = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "test image content".getBytes());

        Mockito.when(placeService.addPlace(Mockito.any(), Mockito.any()))
        .thenReturn("Place with name Test Fort saved successfully, and image uploads initiated.");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/place/add-place")
                .file(placePart)
                .file(imagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated()) // 201 Created
            .andExpect(content().string("Place with name " + placeDTO.getPlaceName() + " saved successfully, and image uploads initiated."));

    }
    
    @Test
    void testAddPlaceDetailsError() throws Exception {
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setPlaceName("Invalid Place");

        String json = objectMapper.writeValueAsString(placeDTO);

        MockMultipartFile placePart = new MockMultipartFile("placeDto", "", "application/json", json.getBytes());
        MockMultipartFile imagePart = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "invalid".getBytes());

        Mockito.when(placeService.addPlace(Mockito.any(), Mockito.any()))
                .thenThrow(new RuntimeException("Simulated failure"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/place/add-place")
                        .file(placePart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());   
      }
    
    
    @Test
    void testGetPlaceDetails_success() throws Exception {
        Long placeId = 1L;

        Mockito.when(placeService.getPlace(placeId)).thenReturn(mockPlaceResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/place/get/{placeId}", placeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placeId").value(1))
                .andExpect(jsonPath("$.placeName").value("Taj Mahal"))
                .andExpect(jsonPath("$.aboutPlace").value("A historical monument."))
                .andExpect(jsonPath("$.placeDistrict").value("Agra"))
                .andExpect(jsonPath("$.rating").value(4.8))
                .andExpect(jsonPath("$.latitude").value(27.1751))
                .andExpect(jsonPath("$.longitude").value(78.0421))
                .andExpect(jsonPath("$.openingTime").value("06:00:00"))
                .andExpect(jsonPath("$.closingTime").value("18:00:00"));
    }

    @Test
    void testGetPlaceDetails_invalidId() throws Exception {
        Long invalidPlaceId = 99L;

        Mockito.when(placeService.getPlace(invalidPlaceId))
                .thenThrow(new IllegalArgumentException("Invalid Place Id.."));

        mockMvc.perform(MockMvcRequestBuilders.get("/place/get/{placeId}", invalidPlaceId))
                .andExpect(status().isBadRequest()); 
    }
    
    
    @Test
    void testUpdatePlaceDetails_success() throws Exception {
        Long placeId = 1L;

        // Prepare DTO with updated values
        PlaceDTO dto = new PlaceDTO();
        dto.setPlaceName("Updated Taj Mahal");
        dto.setAboutPlace("Updated description.");
        dto.setPlaceHistory("Updated history.");
        dto.setPlaceDistrict("Agra");
        dto.setRating(4.9);

        CoordinateDTO coordinate = new CoordinateDTO();
        coordinate.setLatitude(27.1751);
        coordinate.setLongitude(78.0421);
        dto.setCoordinate(coordinate);

        TimezoneDTO timeZone = new TimezoneDTO();
        timeZone.setOpeningTime(LocalTime.of(6, 0));
        timeZone.setClosingTime(LocalTime.of(18, 0));
        dto.setTimeZone(timeZone);

        // Mock the service response
        String successMessage = "Place with ID 1 updated successfully.";
        Mockito.when(placeService.updatePlace(eq(placeId), any(PlaceDTO.class)))
               .thenReturn(successMessage);

        // Perform PUT request
        mockMvc.perform(put("/place/update/{placeId}", placeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String actual = result.getResponse().getContentAsString().trim();
                    assertEquals(successMessage, actual);
                });
    }
    
    @Test
    void testUpdatePlaceDetails_InvalidPlaceId() throws Exception {
        Long invalidPlaceId = 999L;
        
        PlaceDTO dto = new PlaceDTO();
        dto.setPlaceName("Taj Mahal");
        dto.setAboutPlace("Good Ancient Place");
        dto.setPlaceHistory("Historical fort in the city of Delhi");
        dto.setPlaceDistrict("Delhi-Agra");
        dto.setRating(4.0);
        // Add other nested objects like Coordinate or TimeZone if needed

        // Mock behavior: throw exception when invalid ID is used
        Mockito.when(placeService.updatePlace(Mockito.eq(invalidPlaceId), Mockito.any(PlaceDTO.class)))
               .thenThrow(new IllegalArgumentException("Invalid Place Id.."));

        mockMvc.perform(put("/place/update/{placeId}", invalidPlaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())  
                .andExpect(content().string("Invalid Place Id.."));
    }

    
    
    @Test
    void testDeletePlaceDetails_success() throws Exception {
        Long placeId = 1L;
        String expectedMessage = "Place With Id ::1 Deleted Successfully.";

        // Mocking the service call
        Mockito.when(placeService.deletePlace(placeId)).thenReturn(expectedMessage);

        // Perform DELETE request
        mockMvc.perform(delete("/place/delete/{placeId}", placeId))
               .andExpect(status().isOk())
               .andExpect(content().string(expectedMessage));
    }
    
    
    @Test
    void testDeletePlaceDetails_placeNotFound() throws Exception {
        Long placeId = 999L;

        Mockito.when(placeService.deletePlace(placeId))
               .thenThrow(new EntityNotFoundException("Place not found with ID: " + placeId));

        mockMvc.perform(delete("/place/delete/{placeId}", placeId))
               .andExpect(status().isNotFound())
               .andExpect(content().string("Place not found with ID: " + placeId));
    }
   

    @Test
    void testGetAllPlaces_success() throws Exception {
        List<PlaceResponseDTO> mockPlaces = new ArrayList<PlaceResponseDTO>();
        PlaceResponseDTO response1=new PlaceResponseDTO();
        response1.setPlaceId(1L);
        response1.setPlaceName("Taj Mahal");
        response1.setAboutPlace("Good Place to Visit");
        response1.setCategoryName("Moument");
        response1.setPlaceCategory("Heritage");
        response1.setPlaceDistrict("Agra");
        response1.setLatitude(10.556);
        response1.setLongitude(45.678);
        response1.setOpeningTime(LocalTime.parse("06:30"));
        response1.setClosingTime(LocalTime.parse("18:30"));
        response1.setPlaceHistory("An example of Mughal architecture");
        response1.setRating(4.5);
        
        PlaceResponseDTO response2=new PlaceResponseDTO();
        
        response2.setPlaceId(2L);
        response2.setPlaceName("Red Fort");
        response2.setAboutPlace("Historical fort in the city of Delhi");
        response2.setCategoryName("Monument");
        response2.setPlaceCategory("Heritage");
        response2.setPlaceDistrict("Delhi");
        response2.setLatitude(28.6562);
        response2.setLongitude(77.2410);
        response2.setOpeningTime(LocalTime.parse("09:00"));
        response2.setClosingTime(LocalTime.parse("17:00"));
        response2.setPlaceHistory("A historic fort and former residence of Mughal emperors");
        response2.setRating(4.6);

        mockPlaces.add(response1);
        mockPlaces.add(response2);

        Mockito.when(placeService.getAllPlaces()).thenReturn(mockPlaces);

        mockMvc.perform(get("/place/allplaces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(mockPlaces.size()))
                .andExpect(jsonPath("$[0].placeId").value(1))
                .andExpect(jsonPath("$[0].placeName").value("Taj Mahal"))
                .andExpect(jsonPath("$[0].placeDistrict").value("Agra"))
                .andExpect(jsonPath("$[0].rating").value(4.5));
    }
  
    @Test
    void testGetAllPlaces_emptyList() throws Exception {
        Mockito.when(placeService.getAllPlaces()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/place/allplaces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testGetAllDistinctCategories_success() throws Exception {
        // Prepare mock data
        List<PlaceCategoryDTO> mockCategories = new ArrayList<>();
        PlaceCategoryDTO category1 = new PlaceCategoryDTO(1L,"Taj Mahal","Historical fort in the city of Delhi",2L,"Heritage");
        PlaceCategoryDTO category2 = new PlaceCategoryDTO(2L,"Red Fort","Good Place to Visit",3L,"Historical");
        mockCategories.add(category1);
        mockCategories.add(category2);

        // Mock service
        Mockito.when(placeService.getAllDistinctCategories()).thenReturn(mockCategories);

        // Perform GET request
        mockMvc.perform(get("/place/placeby/categories"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.size()").value(mockCategories.size()))
               .andExpect(jsonPath("$[0].placeId").value(1L))
               .andExpect(jsonPath("$[0].categoryName").value("Heritage"))
               .andExpect(jsonPath("$[1].placeId").value(2L))
               .andExpect(jsonPath("$[1].categoryName").value("Historical"));
    }
    
    @Test
    void testGetAllDistinctCategories_emptyList() throws Exception {
        Mockito.when(placeService.getAllDistinctCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/place/placeby/categories"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testGetPlaceIdByName_Success() throws Exception {
        String placeName = "Red Fort";
        Long expectedPlaceId = 1L;

        Mockito.when(placeService.getPlaceIdByName(placeName)).thenReturn(expectedPlaceId);

        mockMvc.perform(get("/place/getplaceid/{placeName}", placeName))
               .andExpect(status().isOk())
               .andExpect(content().string(expectedPlaceId.toString()));
    }

    @Test
    void testGetPlaceIdByName_NotFound() throws Exception {
        String placeName = "Unknown Place";

        Mockito.when(placeService.getPlaceIdByName(placeName))
               .thenThrow(new IllegalArgumentException("Invalid Place Name"));

        mockMvc.perform(get("/place/getplaceid/{placeName}", placeName))
               .andExpect(status().isBadRequest())  // Expect 400 instead of 404
               .andExpect(content().string("Invalid Place Name"));
    }
    
    @AfterEach
    void tearDown() {
        Mockito.reset(placeService);
    }

}
