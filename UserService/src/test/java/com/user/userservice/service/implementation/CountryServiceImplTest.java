package com.user.userservice.service.implementation;

import com.user.userservice.dto.CountryListDTO;
import com.user.userservice.repository.CountryRepository;
import com.user.userservice.service.CountryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {
    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private CountryServiceImpl countryService;

    @Test
    void fetchAllCountries() {
        when(countryRepository.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<CountryListDTO> response = countryService.fetchAllCountries();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

}