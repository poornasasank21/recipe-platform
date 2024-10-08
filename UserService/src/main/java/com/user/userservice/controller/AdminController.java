package com.user.userservice.controller;

import com.user.userservice.dto.*;
import com.user.userservice.exception.CountryAlreadyExistsException;
import com.user.userservice.exception.CountryIdNotFoundException;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.exception.UserIdNotFoundException;
import com.user.userservice.feignclient.RecipeServiceClient;
import com.user.userservice.service.AdminService;
import com.user.userservice.service.CountryService;
import com.user.userservice.service.CuisineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admins")
public class AdminController {

    public final CuisineService cuisineService;
    private static final String CUISINE_NOT_FOUND_MESSAGE = "Cuisine not found with id: ";
    private final CountryService countryService;
    private final AdminService adminService;
    private final RecipeServiceClient recipeServiceClient;


    @PostMapping("/countries")
    public ResponseEntity<CountryDTO> saveCountry(@RequestBody @Valid CountryDTO countryDTO) throws MethodArgumentNotValidException, CountryAlreadyExistsException, CountryAlreadyExistsException {

        CountryDTO savedCountry = countryService.saveCountry(countryDTO);
        return ResponseEntity.ok().body(savedCountry);
    }

    @PutMapping("editUser/{id}")
    public ResponseEntity<AdminUserDTO> editUser(@PathVariable Long id, @RequestBody AdminUserDTO userDTO) throws UserIdNotFoundException {
        AdminUserDTO updatedUserDTO = adminService.updateUser(id, userDTO);
        if (updatedUserDTO != null) {
            return ResponseEntity.ok(updatedUserDTO);
        } else {
            return ResponseEntity.notFound().build();

        }
    }

    @GetMapping("/users")
    public ResponseEntity<UsersResponse> fetchAllUsers() {
        List<AdminDTO> users = adminService.fetchAllUsers();
        UsersResponse response = new UsersResponse(users);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/toggle-user-status")
    public ResponseEntity<ApiResponse> toggleUser(@PathVariable Long id) throws UserIdNotFoundException {
        boolean status = adminService.toggleUserStatus(id);
        String message = status ? "User Enabled successfully" : "User disabled successfully";
        ApiResponse response = ApiResponse.builder()
                .response(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/cuisines")
    public ResponseEntity<CuisineDTO> saveCuisine(@RequestParam("name") String name,
                                                  @RequestParam("enabled") boolean isEnabled,
                                                  @RequestPart("file") MultipartFile file) {
        CuisineDTO addedCuisine = cuisineService.addCuisine(name, isEnabled, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedCuisine);
    }


    @DeleteMapping("/cuisines/{id}")
    public ResponseEntity<ApiResponse> deleteCuisine(@PathVariable Long id) {
        cuisineService.deleteCuisine(id);
        return buildApiResponse("Cuisine deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/cuisines/{id}")
    public ResponseEntity<CuisineDTO> updateCuisine(@PathVariable Long id,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("enabled") boolean isEnabled,
                                                    @RequestPart("file") MultipartFile file) {
        CuisineDTO updatedCuisine = cuisineService.updateCuisine(id, name, isEnabled, file);
        return ResponseEntity.ok(updatedCuisine);
    }

    @PutMapping("/toggle-cuisine/{id}")
    public ResponseEntity<ApiResponse> toggleCuisineEnabled(@PathVariable Long id) {
        String message = cuisineService.toggleCuisineEnabled(id);
        return buildApiResponse(message, HttpStatus.OK);
    }

    private ResponseEntity<ApiResponse> buildApiResponse(String message, HttpStatus status) {
        ApiResponse response = ApiResponse.builder()
                .response(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/cuisines")
    public ResponseEntity<CuisineResponse> getAllCuisines() {
        List<CuisineDTO> cuisines = cuisineService.getAllCuisines();
        return buildCuisineResponse(cuisines, "All cuisines fetched successfully");
    }

    private ResponseEntity<CuisineResponse> buildCuisineResponse(List<CuisineDTO> cuisines, String message) {
        CuisineResponse response = CuisineResponse.builder()
                .cuisines(cuisines)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("countries/edit")
    public ResponseEntity<CountryDTO> editCountry(@RequestBody @Valid CountryDTO countryDTO) throws MethodArgumentNotValidException, CountryAlreadyExistsException {

        CountryDTO savedCountry = countryService.editCountry(countryDTO);
        return ResponseEntity.ok().body(savedCountry);
    }
    @PostMapping("countries/toggle-status")
    public ResponseEntity<ApiResponse> toggleCountry(@RequestBody CountryDTO countryDTO) throws CountryIdNotFoundException {
        boolean status = countryService.toggleCountryStatus(countryDTO.getId());
        String message = status ? "Country Enabled successfully" : "Country disabled successfully";
        ApiResponse response = ApiResponse.builder()
                .response(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/recipes/filter")
    public ResponseEntity<RecipeListDTO> fetchAllRecipesByFilters(
            @RequestParam(required = false) Long cuisineId,
            @RequestParam(required = false) Long categoryId
    ) throws InvalidInputException {
        return adminService.fetchAllRecipesByFilters(cuisineId, categoryId);
    }


    @PutMapping("recipes/{id}/status/{status}")
    public ResponseEntity<SuccessResponse> editRecipeStatus(@PathVariable("id") String id, @PathVariable("status") String status) throws InvalidInputException {
        return recipeServiceClient.editRecipeStatus(id, status);
    }
}

