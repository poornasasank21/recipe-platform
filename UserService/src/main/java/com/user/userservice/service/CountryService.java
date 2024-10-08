package com.user.userservice.service;

import com.user.userservice.dto.CountryDTO;
import com.user.userservice.dto.CountryListDTO;
import com.user.userservice.entity.Country;
import com.user.userservice.exception.CountryAlreadyExistsException;
import com.user.userservice.exception.CountryIdNotFoundException;
import org.springframework.http.ResponseEntity;

public interface CountryService {
    public CountryDTO saveCountry(CountryDTO countryDTO) throws CountryAlreadyExistsException;

    CountryDTO editCountry(CountryDTO countryDTO) throws CountryAlreadyExistsException;

    boolean toggleCountryStatus(Long userId) throws CountryIdNotFoundException;

    Country fetchCountryById(String country);

    ResponseEntity<CountryListDTO> fetchAllCountries();
}
