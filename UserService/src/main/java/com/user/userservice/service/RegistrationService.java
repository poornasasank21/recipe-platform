package com.user.userservice.service;

import com.user.userservice.dto.ApiResponse;
import com.user.userservice.dto.CountryListDTO;
import com.user.userservice.dto.UserRegistrationDTO;
import com.user.userservice.entity.Country;
import com.user.userservice.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RegistrationService {
    ResponseEntity<ApiResponse> register(UserRegistrationDTO userRegistrationDTO) throws IOException;
}
