package com.user.userservice.service;
import com.user.userservice.dto.AdminUserDTO;
import com.user.userservice.dto.CountryDTO;
import com.user.userservice.dto.RecipeListDTO;
import com.user.userservice.dto.RecipeStatusChangeDTO;
import com.user.userservice.entity.Country;
import com.user.userservice.entity.User;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.feignclient.RecipeServiceClient;
import com.user.userservice.repository.CountryRepository;
import com.user.userservice.repository.UserRepository;
import com.user.userservice.dto.AdminDTO;
import com.user.userservice.exception.UserIdNotFoundException;
import com.user.userservice.service.implementation.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
 class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RecipeServiceClient recipeFeign;
    @InjectMocks
    private AdminServiceImpl adminService;
    private AdminUserDTO userDTO;
    private User user;
    private User newuser;
    private Country country;
    //
    @BeforeEach
    void setUp() {
        user  = new User();
        user.setId(3L);
        country = new Country(2L,"India");
        newuser  = new User();
        newuser.setId(3L);
        newuser.setCountry(new Country(1L,"USA"));
        user.setCountry(country);
        userDTO = new AdminUserDTO();
        CountryDTO countryDTO=new CountryDTO();
        countryDTO.setId(1L);
        countryDTO.setName("USA");
        userDTO.setCountry(countryDTO);
    }

    @Test
    void testConvertToDTO() {
        User user = new User();
        AdminUserDTO userDTO = new AdminUserDTO();
        when(modelMapper.map(user, AdminUserDTO.class)).thenReturn(userDTO);
        AdminUserDTO result = adminService.convertToDTO(user);
        assertNotNull(result);
        verify(modelMapper).map(user, AdminUserDTO.class);
    }
    @Test
    void testConvertToEntity() {
        AdminUserDTO userDTO = new AdminUserDTO();
        User user = new User();
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        User result = adminService.convertToEntity(userDTO, User.class);
        assertNotNull(result);
        verify(modelMapper).map(userDTO, User.class);
    }
    @Test
    void testGetUsers() {
        // Arrange
        User user1 = createUserEntity(1L, "John", "Doe", "john.doe@example.com", "USA", true);
        User user2 = createUserEntity(2L, "Jane", "Doe", "jane.doe@example.com", "Canada", false);
        List<User> users = Arrays.asList(user1, user2);

        // Mock the userRepository to return the list of users when findByRole is called
        when(userRepository.findByRole("USER")).thenReturn(users);

        // Act
        List<AdminDTO> result = adminService.fetchAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        AdminDTO userDto1 = result.get(0);
        assertEquals("John", userDto1.getFirstName());
        assertEquals("Doe", userDto1.getLastName());
        assertEquals("john.doe@example.com", userDto1.getEmail());
        assertEquals("USA", userDto1.getCountry());
        assertTrue(userDto1.getEnabled());
        AdminDTO userDto2 = result.get(1);
        assertEquals("Jane", userDto2.getFirstName());
        assertEquals("Doe", userDto2.getLastName());
        assertEquals("jane.doe@example.com", userDto2.getEmail());
        assertEquals("Canada", userDto2.getCountry());
        assertFalse(userDto2.getEnabled());

        // Verify that findByRole("USER") was called exactly once
        verify(userRepository, times(1)).findByRole("USER");
    }

    @Test
    void testToggleUserStatus_UserExistsAndIsDisabled() throws UserIdNotFoundException {
        // Arrange
        User user = createUserEntity(1L, "John", "Doe", "john.doe@example.com", "USA", false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        boolean isEnabled = adminService.toggleUserStatus(1L);

        // Assert
        assertTrue(isEnabled);
        assertTrue(user.getEnabled());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testToggleUserStatus_UserExistsAndIsEnabled() throws UserIdNotFoundException {
        // Arrange
        User user = createUserEntity(1L, "John", "Doe", "john.doe@example.com", "USA", true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        boolean isEnabled = adminService.toggleUserStatus(1L);

        // Assert
        assertFalse(isEnabled);
        assertFalse(user.getEnabled());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testToggleUserStatus_UserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UserIdNotFoundException.class, () -> {
            adminService.toggleUserStatus(1L);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    private User createUserEntity(Long id, String firstName, String lastName, String email, String countryName, boolean enabled) {
        Country country = new Country();
        country.setName(countryName);
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setCountry(country);
        user.setEnabled(enabled);
        return user;
    }

    @Test
    void testFetchAllRecipesByFiltersReturnsRecipes() throws InvalidInputException {
        Long cuisineId = 1L;
        Long categoryId = 1L;

        RecipeListDTO recipeListDTO = createFakeRecipeList();
        User user = createFakeUserBuilder("test@example.com").build();

        when(recipeFeign.fetchAllRecipesByFilters(cuisineId, categoryId)).thenReturn(ResponseEntity.ok(recipeListDTO));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ResponseEntity<RecipeListDTO> response = adminService.fetchAllRecipesByFilters(cuisineId, categoryId);

        assertEquals("test@example.com", response.getBody().getRecipeList().get(0).getEmail());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testFetchAllRecipesByFiltersUserNotFoundThrowsException() {
        Long cuisineId = 1L;
        Long categoryId = 1L;

        RecipeListDTO recipeListDTO = createFakeRecipeList();

        when(recipeFeign.fetchAllRecipesByFilters(cuisineId, categoryId)).thenReturn(ResponseEntity.ok(recipeListDTO));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(InvalidInputException.class, () -> {
            adminService.fetchAllRecipesByFilters(cuisineId, categoryId);
        });
    }

    private RecipeListDTO createFakeRecipeList() {
        RecipeStatusChangeDTO recipeStatus = RecipeStatusChangeDTO.builder()
                .id(1L)
                .userId(1L)
                .email("test@example.com")
                .name("Recipe1")
                .build();

        return RecipeListDTO.builder()
                .timestamp("2021-01-01T00:00:00Z")
                .status("OK")
                .recipeList(Collections.singletonList(recipeStatus))
                .build();
    }

    private User.UserBuilder createFakeUserBuilder(String email) {
        return User.builder()
                .email(email);
    }
}
