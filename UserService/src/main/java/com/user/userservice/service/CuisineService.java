package com.user.userservice.service;

import com.user.userservice.dto.CuisineDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CuisineService {
    List<CuisineDTO> getAllCuisines();
    CuisineDTO addCuisine(String name, boolean isEnabled, MultipartFile file);
    void deleteCuisine(Long id);
    CuisineDTO updateCuisine(Long id, String name, boolean isEnabled, MultipartFile file);
    String toggleCuisineEnabled(Long id);
    boolean checkCuisineExistsById(Long id);
    boolean checkCuisineNameExists(String name);
}
