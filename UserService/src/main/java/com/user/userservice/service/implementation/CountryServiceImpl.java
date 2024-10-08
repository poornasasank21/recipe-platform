package com.user.userservice.service.implementation;

import com.user.userservice.constants.ErrorConstants;
import com.user.userservice.dto.CountryListDTO;
import com.user.userservice.exception.CountryAlreadyExistsException;
import com.user.userservice.exception.CountryIdNotFoundException;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.repository.CountryRepository;
import com.user.userservice.dto.CountryDTO;
import com.user.userservice.entity.Country;
import com.user.userservice.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final ModelMapper modelMapper;

    public CountryDTO saveCountry(CountryDTO countryDTO) throws CountryAlreadyExistsException {
        Optional<Country> existingCountry = countryRepository.findByName(countryDTO.getName().toLowerCase());
        if (existingCountry.isPresent()) {
            throw  new CountryAlreadyExistsException(existingCountry.get().getName()+" is Already Added");

        }
        String formattedName = countryDTO.getName().substring(0, 1).toUpperCase() + countryDTO.getName().substring(1).toLowerCase();
        countryDTO.setName(formattedName);
        Country country = modelMapper.map(countryDTO, Country.class);
        country = countryRepository.save(country);
        return modelMapper.map(country, CountryDTO.class);
    }

    public CountryDTO editCountry(CountryDTO countryDTO) throws CountryAlreadyExistsException {
        Optional<Country> existingCountry = countryRepository.findByName(countryDTO.getName().toLowerCase());
        if (existingCountry.isPresent()) {
            throw  new CountryAlreadyExistsException(existingCountry.get().getName()+" is Already Added");

        }
        Optional<Country>getCountry=countryRepository.findById(countryDTO.getId());
        String formattedName = countryDTO.getName().substring(0, 1).toUpperCase() + countryDTO.getName().substring(1).toLowerCase();
        countryDTO.setId(getCountry.get().getId());
        countryDTO.setName(formattedName);
        Country country = modelMapper.map(countryDTO, Country.class);
        country = countryRepository.save(country);
        return modelMapper.map(country, CountryDTO.class);
    }

    public boolean toggleCountryStatus(Long userId) throws CountryIdNotFoundException {
        Country country = countryRepository.findById(userId)
                .orElseThrow(() -> new CountryIdNotFoundException("User not found with ID: " + userId));
        country.setEnabled(!country.isEnabled());
        countryRepository.save(country);
        return country.isEnabled();
    }

    @Override
    public Country fetchCountryById(String country) {
        long id = Long.parseLong(country);
        Optional<Country> optionalCountry = countryRepository.findById(id);

        if (optionalCountry.isPresent()) {
            return optionalCountry.get();
        } else {
            throw new InvalidInputException(ErrorConstants.COUNTRY_NOT_FOUND + country);
        }
    }

    @Override
    public ResponseEntity<CountryListDTO> fetchAllCountries() {
        List<Country> countryList = countryRepository.findAll();
        CountryListDTO countryListDTO = CountryListDTO.builder()
                .countryList(countryList)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.name())
                .build();

        return ResponseEntity.ok(countryListDTO);
    }
}
