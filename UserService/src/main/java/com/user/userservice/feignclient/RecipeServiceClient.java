package com.user.userservice.feignclient;

import com.user.userservice.dto.UpdateRecipeDTO;
import com.user.userservice.dto.CuisineDTO;
import com.user.userservice.dto.AddRecipeDTO;
import com.user.userservice.dto.RecipeListDTO;
import com.user.userservice.dto.SuccessResponse;
import com.user.userservice.dto.AllCommentsDTO;
import com.user.userservice.exception.IdNotFoundException;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.exception.UserIdNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.io.IOException;
import java.util.List;

@FeignClient(name = "recipe-service", path = "/api/recipes")
public interface RecipeServiceClient {

    @PostMapping("/cuisines")
    CuisineDTO addCuisine(@RequestBody CuisineDTO cuisineDTO);
    @PutMapping("/cuisines/disable/{id}")
    void disableCuisine(@PathVariable("id") Long id);
    @DeleteMapping("/cuisines/{id}")
    void deleteCuisine(@PathVariable("id") Long id);
    @PutMapping("/cuisines/enable/{id}")
    void enableCuisine(@PathVariable("id") Long id);
    @PutMapping("/cuisines/{id}")
    CuisineDTO updateCuisine(@PathVariable("id") Long id, @RequestBody CuisineDTO cuisineDTO);
    @GetMapping("/cuisines")
    List<CuisineDTO> getAllCuisines();
    @GetMapping("/cuisines/exist/by-id")
    public ResponseEntity<Boolean> doesCuisineExistById(@RequestParam Long id);
    @GetMapping("/cuisines/exist/by-name")
    public ResponseEntity<Boolean> doesCuisineExistByName(@RequestParam String name);
    @GetMapping("/cuisines/{id}/is-enabled")
    public Boolean isCuisineEnabled(@PathVariable Long id);
    @PutMapping(value="/update",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateRecipe(@ModelAttribute UpdateRecipeDTO recipeDTO) throws IdNotFoundException, IOException;
    @GetMapping("admins/filter")
    public ResponseEntity<RecipeListDTO> fetchAllRecipesByFilters(
            @RequestParam(required = false) Long cuisineId,
            @RequestParam(required = false) Long categoryId
    ) throws InvalidInputException;
    @PutMapping("{id}/status/{status}")
    public ResponseEntity<SuccessResponse> editRecipeStatus(@PathVariable("id") String id, @PathVariable("status") String status) throws InvalidInputException;
    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable("id") Long id) throws InvalidInputException;
    @GetMapping("getUserIdByRecipeId/{id}")
    public ResponseEntity<String> getRecipeOwnerId(@PathVariable("id") Long recipeId) throws InvalidInputException;
    @PostMapping(value="/save", consumes = "multipart/form-data")
    public ResponseEntity<?> addRecipe(@ModelAttribute @Valid AddRecipeDTO addRecipeDto) throws UserIdNotFoundException, NotFoundException;
    @GetMapping("/comments/{recipeId}")
    public ResponseEntity<AllCommentsDTO> getAllComments(@PathVariable Long recipeId);
}