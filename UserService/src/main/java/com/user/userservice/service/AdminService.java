package com.user.userservice.service;

import com.user.userservice.dto.AdminDTO;
import com.user.userservice.dto.AdminUserDTO;
import com.user.userservice.dto.RecipeListDTO;
import com.user.userservice.entity.User;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.exception.UserIdNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService {
    public AdminUserDTO updateUser(Long id, AdminUserDTO user) throws UserIdNotFoundException;
    public AdminUserDTO convertToDTO(User user);
    public  <T> T convertToEntity(Object dto, Class<T> entityClass);
    List<AdminDTO> fetchAllUsers();
    boolean toggleUserStatus(Long userId) throws UserIdNotFoundException;

    ResponseEntity<RecipeListDTO> fetchAllRecipesByFilters(Long cuisineId, Long categoryId) throws InvalidInputException;
}
