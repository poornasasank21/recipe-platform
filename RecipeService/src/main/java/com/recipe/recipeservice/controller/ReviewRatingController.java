package com.recipe.recipeservice.controller;

import com.recipe.recipeservice.dto.AddReviewDTO;
import com.recipe.recipeservice.dto.AverageRatingDTO;
import com.recipe.recipeservice.dto.RecipeReviewDTO;
import com.recipe.recipeservice.dto.ReviewDataDTO;
import com.recipe.recipeservice.dto.ReviewResponseDTO;
import com.recipe.recipeservice.dto.UpdateReviewDTO;
import com.recipe.recipeservice.entity.ReviewRating;
import com.recipe.recipeservice.exception.ResourceNotFoundException;
import com.recipe.recipeservice.exception.ReviewNotFoundException;
import com.recipe.recipeservice.service.ReviewRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("recipes/api/reviews")
public class ReviewRatingController {

    @Autowired
    private ReviewRatingService reviewRatingService;

    @PostMapping
    public ResponseEntity<ReviewRating> addReview(@RequestBody AddReviewDTO addReviewDTO) {
        ReviewRating savedReview = reviewRatingService.addReview(addReviewDTO);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ReviewRating> editReview(@RequestBody UpdateReviewDTO updateReviewDTO) throws ResourceNotFoundException {
        ReviewRating updatedReview = reviewRatingService.updateReview(updateReviewDTO);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @PostMapping("/userReview")
    public ResponseEntity<ReviewResponseDTO> getUserReview(@RequestBody RecipeReviewDTO recipeReviewDTO) throws ReviewNotFoundException {
        if (recipeReviewDTO.getUserId() == null || recipeReviewDTO.getRecipeId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ReviewRating reviewRating = reviewRatingService.getUserReview(recipeReviewDTO.getRecipeId(), recipeReviewDTO.getUserId());
        ReviewResponseDTO reviewResponseDTO = ReviewResponseDTO.builder()
                .isReviewed(true)
                .data(ReviewDataDTO.builder()
                        .id(reviewRating.getId())
                        .userId(reviewRating.getUserId())
                        .recipeId(reviewRating.getRecipeId())
                        .rating(reviewRating.getRating())
                        .description(reviewRating.getDescription())
                        .createdAt(reviewRating.getCreatedAt())
                        .build())
                .build();

        return ResponseEntity.ok(reviewResponseDTO);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long recipeId, @RequestHeader("userId") Long userId) {
        reviewRatingService.deleteReview(userId, recipeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{recipeId}/averageRating")
    public ResponseEntity<AverageRatingDTO> getAverageRating(@PathVariable Long recipeId) throws ResourceNotFoundException {
        Double averageRating = reviewRatingService.getAverageRating(recipeId);
        AverageRatingDTO averageRatingDTO = new AverageRatingDTO(averageRating);
        return new ResponseEntity<>(averageRatingDTO, HttpStatus.OK);
    }
}
