package com.user.userservice.service.implementation;

import com.user.userservice.constants.ErrorConstants;
import com.user.userservice.dto.ApiResponse;
import com.user.userservice.dto.UserRegistrationDTO;
import com.user.userservice.entity.User;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.repository.UserRepository;
import com.user.userservice.service.CountryService;
import com.user.userservice.service.ImageService;
import com.user.userservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    @Value("${project.image}")
    private String path;

    @Override
    public ResponseEntity<ApiResponse> register(UserRegistrationDTO userRegistrationDTO) throws IOException {
        if (userRepository.existsByEmail(userRegistrationDTO.getEmail())) {
            throw new InvalidInputException(ErrorConstants.EMAIL_EXISTS + userRegistrationDTO.getEmail());
        }

        userRepository.save(mapUserRegistrationDTOtoUser(userRegistrationDTO));
        ApiResponse response = ApiResponse.builder()
                .response("Registration Successful")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    public User mapUserRegistrationDTOtoUser(UserRegistrationDTO userRegistrationDTO) throws IOException {
        User user = modelMapper.map(userRegistrationDTO, User.class);

        user.setCountry(countryService.fetchCountryById(userRegistrationDTO.getCountry()));
        user.setDate(LocalDate.now());
        user.setEnabled(true);
        user.setImage(imageService.uploadImage(path, userRegistrationDTO.getFile()));
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setRole("USER");

        return user;
    }
}
