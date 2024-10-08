package com.user.userservice.controller;
import com.user.userservice.dto.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.user.userservice.service.CuisineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.user.userservice.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.user.userservice.exception.UserIdNotFoundException;
import com.user.userservice.service.AdminService;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    private MockMvc mockMvc;
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminController adminController;
    @Mock
    private CountryService countryService;
    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(adminController).build();
    }
    @Mock
    public CuisineService cuisineService;
    @Test
    void getAllCuisines_ShouldReturnCuisineResponse() {
        List<CuisineDTO> mockCuisines = Arrays.asList(
                new CuisineDTO(1L, "Italian", true, "image1.jpg"),
                new CuisineDTO(2L, "Mexican", true, "image2.jpg")
        );
        when(cuisineService.getAllCuisines()).thenReturn(mockCuisines);
        ResponseEntity<CuisineResponse> response = adminController.getAllCuisines();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getCuisines().size());
        assertEquals("All cuisines fetched successfully", response.getBody().getMessage());
        verify(cuisineService, times(1)).getAllCuisines();
    }

    @Test
    void saveCuisine_ShouldAddCuisineAndReturnCreatedCuisine() {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image data".getBytes());
        CuisineDTO mockCuisine = new CuisineDTO(1L, "French", true, "image.jpg");

        when(cuisineService.addCuisine(anyString(), anyBoolean(), any(MultipartFile.class))).thenReturn(mockCuisine);
        ResponseEntity<CuisineDTO> response = adminController.saveCuisine("French", true, file);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockCuisine, response.getBody());
        verify(cuisineService, times(1)).addCuisine(anyString(), anyBoolean(), any(MultipartFile.class));
    }
    @Test
    void deleteCuisine_ShouldReturnSuccessMessage() {
        ResponseEntity<ApiResponse> response = adminController.deleteCuisine(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cuisine deleted successfully", response.getBody().getResponse());
        verify(cuisineService, times(1)).deleteCuisine(1L);
    }

    @Test
    void updateCuisine_ShouldReturnUpdatedCuisine() {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image data".getBytes());
        CuisineDTO updatedCuisine = new CuisineDTO(1L, "Chinese", true, "image.jpg");

        when(cuisineService.updateCuisine(eq(1L), anyString(), anyBoolean(), any(MultipartFile.class))).thenReturn(updatedCuisine);
        ResponseEntity<CuisineDTO> response = adminController.updateCuisine(1L, "Chinese", true, file);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCuisine, response.getBody());
        verify(cuisineService, times(1)).updateCuisine(eq(1L), anyString(), anyBoolean(), any(MultipartFile.class));
    }

    @Test
    void toggleCuisineEnabled_ShouldReturnToggleMessage() {
        when(cuisineService.toggleCuisineEnabled(1L)).thenReturn("Cuisine enabled successfully");
        ResponseEntity<ApiResponse> response = adminController.toggleCuisineEnabled(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cuisine enabled successfully", response.getBody().getResponse());
        verify(cuisineService, times(1)).toggleCuisineEnabled(1L);
    }



    @Test
    void testEditUser_Success() throws UserIdNotFoundException {
        Long userId = 1L;
        AdminUserDTO userDTO = new AdminUserDTO();
        AdminUserDTO updatedUserDTO = new AdminUserDTO();
        when(adminService.updateUser(userId, userDTO)).thenReturn(updatedUserDTO);
        ResponseEntity<AdminUserDTO> response = adminController.editUser(userId, userDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUserDTO, response.getBody());
        verify(adminService).updateUser(userId, userDTO);
    }

    @Test
    void testEditUser_NotFound() throws UserIdNotFoundException {
        Long userId = 1L;
        AdminUserDTO userDTO = new AdminUserDTO();
        when(adminService.updateUser(userId, userDTO)).thenReturn(null);
        ResponseEntity<AdminUserDTO> response = adminController.editUser(userId, userDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(adminService).updateUser(userId, userDTO);
    }

    @Test
    void testEditUser_ThrowsException() throws UserIdNotFoundException {
        Long userId = 1L;
        AdminUserDTO userDTO = new AdminUserDTO();
        when(adminService.updateUser(userId, userDTO)).thenThrow(new UserIdNotFoundException("User not found"));
        assertThrows(UserIdNotFoundException.class, () -> {
            adminController.editUser(userId, userDTO);
        });
        verify(adminService).updateUser(userId, userDTO);
    }

    @Test
    void testAddCountry() throws Exception {
        // Arrange
        CountryDTO newCountry = new CountryDTO();
        newCountry.setId(1L);
        newCountry.setName("Mexico");

        CountryDTO savedCountry = new CountryDTO();
        savedCountry.setId(3L);
        savedCountry.setName("Mexico");
        when(countryService.saveCountry(any(CountryDTO.class))).thenReturn(savedCountry);
        mockMvc.perform(post("/admins/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Mexico\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mexico"))
                .andExpect(jsonPath("$.id").value(3));
        verify(countryService).saveCountry(any(CountryDTO.class));
    }
    @Test
    void testFetchAllUsers() {
        // Arrange
        List<AdminDTO> users = new ArrayList<>();
        users.add(new AdminDTO(1L, "John", "Doe", "john.doe@example.com", LocalDate.now(), true, "USA"));
        when(adminService.fetchAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<UsersResponse> responseEntity = adminController.fetchAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(users, responseEntity.getBody().getUsers());
        verify(adminService, times(1)).fetchAllUsers();
    }

    // Test for toggleUser (enabling a user)
    @Test
    void testToggleUserStatus_EnableUser() throws UserIdNotFoundException {
        // Arrange
        Long userId = 1L;
        when(adminService.toggleUserStatus(userId)).thenReturn(true);  // User enabled

        // Act
        ResponseEntity<ApiResponse> responseEntity = adminController.toggleUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User Enabled successfully", responseEntity.getBody().getResponse());
        verify(adminService, times(1)).toggleUserStatus(userId);
    }

    // Test for toggleUser (disabling a user)
    @Test
    void testToggleUserStatus_DisableUser() throws UserIdNotFoundException {
        // Arrange
        Long userId = 1L;
        when(adminService.toggleUserStatus(userId)).thenReturn(false);  // User disabled

        // Act
        ResponseEntity<ApiResponse> responseEntity = adminController.toggleUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User disabled successfully", responseEntity.getBody().getResponse());
        verify(adminService, times(1)).toggleUserStatus(userId);
    }

    // Test for toggleUser (UserIdNotFoundException)
    @Test
    void testToggleUserStatus_UserIdNotFound() throws UserIdNotFoundException {
        // Arrange
        Long userId = 1L;
        when(adminService.toggleUserStatus(userId)).thenThrow(new UserIdNotFoundException("User not found with ID: " + userId));

        // Act & Assert
        try {
            adminController.toggleUser(userId);
        } catch (UserIdNotFoundException e) {
            assertEquals("User not found with ID: 1", e.getMessage());
        }

        verify(adminService, times(1)).toggleUserStatus(userId);
    }
}