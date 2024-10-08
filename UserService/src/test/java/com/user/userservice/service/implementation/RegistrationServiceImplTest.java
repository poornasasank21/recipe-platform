package com.user.userservice.service.implementation;

import com.user.userservice.dto.ApiResponse;
import com.user.userservice.dto.UserRegistrationDTO;
import com.user.userservice.entity.Country;
import com.user.userservice.entity.User;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.repository.UserRepository;
import com.user.userservice.service.CountryService;
import com.user.userservice.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {
    @InjectMocks
    private RegistrationServiceImpl registrationService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryService countryService;
    @Mock
    private ImageService imageService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        Field pathField = RegistrationServiceImpl.class.getDeclaredField("path");
        pathField.setAccessible(true);
        pathField.set(registrationService, "mock/path");
    }

    @Test
    void testRegisterNewUser() throws IOException {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(countryService.fetchCountryById(anyString())).thenReturn(new Country());
        when(modelMapper.map(any(UserRegistrationDTO.class), eq(User.class))).thenReturn(new User());
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(imageService.uploadImage(anyString(), any(MultipartFile.class))).thenReturn("image.jpg");
        when(passwordEncoder.encode(anyString())).thenReturn("password123156464879");

        ResponseEntity<ApiResponse> response = registrationService.register(dummyUserRegistrationDTO());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Registration Successful", response.getBody().getResponse());
    }

    @Test
    void testRegisterUserAlreadyExists() {
        UserRegistrationDTO dto = new UserRegistrationDTO();

        dto.setEmail("existing@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);
        assertThrows(InvalidInputException.class, () -> registrationService.register(dto), "This Email already exists : " + dto.getEmail());
        verify(userRepository).existsByEmail(dto.getEmail());
    }

    public UserRegistrationDTO dummyUserRegistrationDTO() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("newuser@example.com");
        dto.setCountry("1");
        dto.setPassword("password");
        dto.setFile(new MockMultipartFile("file", "test.jpg", "text/plain", "Test content".getBytes()));
        return dto;
    }
}