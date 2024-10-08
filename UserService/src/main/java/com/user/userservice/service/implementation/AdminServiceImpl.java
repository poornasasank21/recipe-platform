package com.user.userservice.service.implementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.userservice.constants.ErrorConstants;
import com.user.userservice.dto.AdminDTO;
import com.user.userservice.dto.AdminUserDTO;
import com.user.userservice.dto.RecipeListDTO;
import com.user.userservice.dto.RecipeStatusChangeDTO;
import com.user.userservice.entity.User;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.exception.UserIdNotFoundException;
import com.user.userservice.feignclient.RecipeServiceClient;
import com.user.userservice.repository.CountryRepository;
import com.user.userservice.repository.UserRepository;
import com.user.userservice.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.user.userservice.entity.Country;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final RecipeServiceClient recipeFeign;
    @Transactional
    public AdminUserDTO updateUser(Long id, AdminUserDTO userDTO) throws UserIdNotFoundException{
        return userRepository.findById(id).map(existingUser -> {
           existingUser= modelMapper.map(userDTO, User.class);
           existingUser.setId(id);
           Country country = countryRepository.findById(userDTO.getCountry().getId()).get();
            existingUser.setCountry(country);

        return convertToDTO(userRepository.save(existingUser));
        }).orElseThrow(()->new UserIdNotFoundException("User ID not found with id "+id));
    }
    public AdminUserDTO convertToDTO(User user) {
         return modelMapper.map(user, AdminUserDTO.class);
    }
    public <T> T convertToEntity(Object dto, Class<T> entityClass) {
        return modelMapper.map(dto, entityClass);
    }
    @Override
    public List<AdminDTO> fetchAllUsers() {
        List<User> userEntities = userRepository.findByRole("USER");
        return userEntities.stream()
                .map(user -> new AdminDTO(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getDate(),
                        user.getEnabled(),
                        user.getCountry().getName() // Get only country name
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean toggleUserStatus(Long userId) throws UserIdNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User not found with ID: " + userId));
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
        return user.getEnabled();
    }

    @Override
    public ResponseEntity<RecipeListDTO> fetchAllRecipesByFilters(Long cuisineId, Long categoryId) throws InvalidInputException {
        RecipeListDTO recipeListDTO = recipeFeign.fetchAllRecipesByFilters(cuisineId, categoryId).getBody();

        List<RecipeStatusChangeDTO> recipeList = recipeListDTO.getRecipeList();
        for (int i = 0; i < recipeList.size(); i++) {
            User user = userRepository.findById(recipeList.get(i).getUserId())
                    .orElseThrow(() -> new InvalidInputException(ErrorConstants.USER_NOT_FOUND_WITH_ID));

            recipeList.get(i).setEmail(user.getEmail());
        }
        recipeListDTO.setRecipeList(recipeList);

        return ResponseEntity.ok(recipeListDTO);
    }
}
