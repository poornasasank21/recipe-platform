package com.recipe.recipeservice.service;
import com.recipe.recipeservice.dto.CuisineDTO;
import java.util.List;

public interface CuisineService {
        CuisineDTO addCuisine(CuisineDTO cuisineDTO);
        boolean disableCuisineById(Long id);
        boolean enableCuisineById(Long id);
        boolean deleteCuisineById(Long id);
        CuisineDTO updateCuisineById(Long id, CuisineDTO cuisineDTO);
        List<CuisineDTO> getEnabledCuisines();
        List<CuisineDTO> getAllCuisines();
        boolean doesCuisineExistByName(String name);
        boolean doesCuisineExistById(Long id);
        boolean isCuisineEnabled(Long id);
}
