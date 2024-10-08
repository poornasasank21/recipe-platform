package com.user.userservice.service;

import com.user.userservice.dto.CountryDTO;
import com.user.userservice.dto.CountryListDTO;
import com.user.userservice.entity.Country;
import com.user.userservice.exception.CountryAlreadyExistsException;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.repository.CountryRepository;
import com.user.userservice.service.implementation.CountryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CountryServiceImpl countryService;

    @Test
    void testSaveNewCountry_Success() throws CountryAlreadyExistsException {
        // Arrange
        String countryName = "Canada";
        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setName(countryName);
        Country newCountry = new Country();
        newCountry.setName(countryName);
        Country savedCountry = new Country();
        savedCountry.setName(countryName);
        when(countryRepository.findByName(countryName.toLowerCase())).thenReturn(Optional.empty());
        when(modelMapper.map(countryDTO, Country.class)).thenReturn(newCountry);
        when(countryRepository.save(newCountry)).thenReturn(savedCountry);
        when(modelMapper.map(savedCountry, CountryDTO.class)).thenReturn(countryDTO);
        CountryDTO result = countryService.saveCountry(countryDTO);
        assertNotNull(result);
        assertEquals(countryName, result.getName());
        verify(countryRepository).findByName(countryName.toLowerCase());
        verify(countryRepository).save(newCountry);
        verify(modelMapper).map(countryDTO, Country.class);
        verify(modelMapper).map(savedCountry, CountryDTO.class);
    }
    @Test
    void testSaveCountry_ThrowsCountryAlreadyExistsException() {
        // Arrange
        String countryName = "Germany";
        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setName(countryName);
        Country existingCountry = new Country();
        existingCountry.setName(countryName);
        when(countryRepository.findByName(countryName.toLowerCase())).thenReturn(Optional.of(existingCountry));
        // Act & Assert
        CountryAlreadyExistsException exception = assertThrows(
                CountryAlreadyExistsException.class,
                () -> countryService.saveCountry(countryDTO),
                "Expected saveCountry to throw, but it didn't"
        );
        assertTrue(exception.getMessage().contains("is Already Added"));
        verify(countryRepository).findByName(countryName.toLowerCase());
        verify(modelMapper, never()).map(any(), eq(Country.class));
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void fetchCountryById() throws InvalidInputException {
        Long countryId = 1L;
        Country expectedCountry = dummyCountry();

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(expectedCountry));
        Country result = countryService.fetchCountryById(String.valueOf(countryId));

        assertNotNull(result, "The country should not be null");
        assertEquals(expectedCountry.getId(), result.getId(), "The country IDs should match");
        assertEquals(expectedCountry.getName(), result.getName(), "The country names should match");

        verify(countryRepository).findById(countryId);
    }

    @Test
    void testFetchCountryById() {
        Country country1 = new Country(1, "India");
        Country country2 = new Country(2, "Canada");
        List<Country> mockCountries = Arrays.asList(country1, country2);
        when(countryRepository.findAll()).thenReturn(mockCountries);

        ResponseEntity<CountryListDTO> response = countryService.fetchAllCountries();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getCountryList().size());
        assertEquals("India", response.getBody().getCountryList().get(0).getName());
        assertEquals("Canada", response.getBody().getCountryList().get(1).getName());
        assertEquals(HttpStatus.OK.name(), response.getBody().getStatus());

        verify(countryRepository).findAll();
    }

    public Country dummyCountry() {
        Country expectedCountry = new Country();

        expectedCountry.setId(1L);
        expectedCountry.setName("Test Country");

        return expectedCountry;
    }

    @Test
    void fetchAllCountries() {
        when(countryRepository.findAll()).thenReturn(new ArrayList<>());

        CountryListDTO countryListDTO = CountryListDTO.builder().countryList(countryRepository.findAll()).build();
        ResponseEntity<CountryListDTO> response = ResponseEntity.ok(countryListDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

}