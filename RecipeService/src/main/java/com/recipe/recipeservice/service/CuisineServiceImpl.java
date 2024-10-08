package com.recipe.recipeservice.service;

import com.recipe.recipeservice.dto.CuisineDTO;
import com.recipe.recipeservice.entity.Cuisine;
import com.recipe.recipeservice.repository.CuisineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CuisineServiceImpl implements CuisineService {
    private final CuisineRepository cuisineRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public CuisineDTO addCuisine(CuisineDTO cuisineDTO) {
        Cuisine cuisine=modelMapper.map(cuisineDTO,Cuisine.class);
        Cuisine savedCuisine = cuisineRepository.save(cuisine);
       return  modelMapper.map(savedCuisine, CuisineDTO.class);
    }

    @Transactional
    @Override
    public boolean disableCuisineById(Long id) {
        return cuisineRepository.findById(id).map(cuisine -> {
            cuisine.setEnabled(false);
            cuisineRepository.save(cuisine);
            return true;
        }).orElse(false);
    }

    @Transactional
    @Override
    public boolean enableCuisineById(Long id) {
        return cuisineRepository.findById(id).map(cuisine -> {
            cuisine.setEnabled(true);
            cuisineRepository.save(cuisine);
            return true;
        }).orElse(false);
    }

    @Transactional
    @Override
    public boolean deleteCuisineById(Long id) {
        if (cuisineRepository.existsById(id)) {
            cuisineRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public CuisineDTO updateCuisineById(Long id, CuisineDTO cuisineDTO) {
        return cuisineRepository.findById(id).map(cuisine -> {
            cuisine.setName(cuisineDTO.getName());
            cuisine.setEnabled(cuisineDTO.isEnabled());
            cuisine.setImageUrl(cuisineDTO.getImageUrl());
            Cuisine updatedCuisine = cuisineRepository.save(cuisine);
            return  cuisineDTO;
        }).orElse(null);
    }

    @Override
    public List<CuisineDTO> getEnabledCuisines() {
        List<Cuisine> cuisines = cuisineRepository.findByIsEnabled(true);
        return cuisines.stream()
                .map(cuisine -> new CuisineDTO(cuisine.getId(), cuisine.getName(), cuisine.isEnabled(), cuisine.getImageUrl()))
                .toList();
    }

    @Override
    public List<CuisineDTO> getAllCuisines() {
        List<Cuisine> cuisines = cuisineRepository.getAllCuisines();
        return cuisines.stream()
                .map(cuisine -> new CuisineDTO(cuisine.getId(), cuisine.getName(), cuisine.isEnabled(), cuisine.getImageUrl()))
                .toList();
    }

    @Override
    public boolean doesCuisineExistByName(String name) {
        return cuisineRepository.doesCuisineExistByName(name);
    }

    @Override
    public boolean doesCuisineExistById(Long id) {
        return cuisineRepository.existsById(id);
    }

    @Override
    public boolean isCuisineEnabled(Long id) {
        return cuisineRepository.findById(id).map(Cuisine::isEnabled).orElse(false);
    }
}
