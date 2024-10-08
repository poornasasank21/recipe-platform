package com.recipe.recipeservice.controller;

import com.recipe.recipeservice.dto.AllCommentsDTO;
import com.recipe.recipeservice.dto.CuisineDTO;
import com.recipe.recipeservice.entity.ReviewRating;
import com.recipe.recipeservice.service.CuisineService;
import com.recipe.recipeservice.service.ReviewRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recipes")
public class RecipeCuisineController {

    private final CuisineService cuisineService;
    private final ReviewRatingService reviewRatingService;

    @PostMapping("/cuisines")
    public ResponseEntity<CuisineDTO> addCuisine(@RequestBody CuisineDTO cuisineDTO) {
        if (cuisineService.doesCuisineExistByName(cuisineDTO.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        CuisineDTO savedCuisine = cuisineService.addCuisine(cuisineDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCuisine);
    }

    @PutMapping("/cuisines/disable/{id}")
    public ResponseEntity<Void> disableCuisine(@PathVariable Long id) {
        if (cuisineService.disableCuisineById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/cuisines/{id}")
    public ResponseEntity<Void> deleteCuisine(@PathVariable Long id) {
        if (cuisineService.deleteCuisineById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/cuisines/enable/{id}")
    public ResponseEntity<Void> enableCuisine(@PathVariable Long id) {
        if (cuisineService.enableCuisineById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/cuisines/{id}")
    public ResponseEntity<CuisineDTO> updateCuisine(@PathVariable Long id, @RequestBody CuisineDTO cuisineDTO) {
        CuisineDTO updatedCuisine = cuisineService.updateCuisineById(id, cuisineDTO);
        if (updatedCuisine != null) {
            return ResponseEntity.ok(updatedCuisine);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cuisines")
    public ResponseEntity<List<CuisineDTO>> getAllCuisines() {
        List<CuisineDTO> cuisineDTOs = cuisineService.getAllCuisines();
        return ResponseEntity.ok(cuisineDTOs);
    }

    @GetMapping("/cuisines/exist/by-name")
    public ResponseEntity<Boolean> doesCuisineExistByName(@RequestParam String name) {
        boolean exists = cuisineService.doesCuisineExistByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/cuisines/exist/by-id")
    public ResponseEntity<Boolean> doesCuisineExistById(@RequestParam Long id) {
        boolean exists = cuisineService.doesCuisineExistById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/cuisines/{id}/is-enabled")
    public ResponseEntity<Boolean> isCuisineEnabled(@PathVariable Long id) {
        return ResponseEntity.ok(cuisineService.isCuisineEnabled(id));
    }

    @GetMapping("/comments/{recipeId}")
    public ResponseEntity<AllCommentsDTO> getAllComments(@PathVariable Long recipeId) {
        List<ReviewRating> comments = reviewRatingService.getAllReviews(recipeId);
        AllCommentsDTO allCommentsDTO = AllCommentsDTO.builder()
                .reviews(comments)
                .build();
        return new ResponseEntity<>(allCommentsDTO, HttpStatus.OK);
    }
}
