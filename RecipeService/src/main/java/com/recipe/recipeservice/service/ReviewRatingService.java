package com.recipe.recipeservice.service;

import com.recipe.recipeservice.dto.AddReviewDTO;
import com.recipe.recipeservice.dto.UpdateReviewDTO;
import com.recipe.recipeservice.entity.ReviewRating;
import com.recipe.recipeservice.exception.ResourceNotFoundException;
import com.recipe.recipeservice.exception.ReviewNotFoundException;
import com.recipe.recipeservice.repository.ReviewRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewRatingService {

    @Autowired
    private ReviewRatingRepository reviewRatingRepository;

    public ReviewRating addReview(AddReviewDTO addReviewDTO) {
        ReviewRating review = new ReviewRating();
        review.setUserId(addReviewDTO.getUserId());
        review.setRecipeId(addReviewDTO.getRecipeId());
        review.setRating(addReviewDTO.getRating());
        review.setDescription(addReviewDTO.getDescription());
        return reviewRatingRepository.save(review);
    }

    public ReviewRating updateReview(UpdateReviewDTO updateReviewDTO)throws ResourceNotFoundException {
        Optional<ReviewRating> reviewOpt = reviewRatingRepository.findByUserIdAndRecipeId(
                updateReviewDTO.getUserId(), updateReviewDTO.getRecipeId());

        if (reviewOpt.isPresent()) {
            ReviewRating review = reviewOpt.get();
            if (updateReviewDTO.getRating() != null) {
                review.setRating(updateReviewDTO.getRating());
            }
            if (updateReviewDTO.getDescription() != null) {
                review.setDescription(updateReviewDTO.getDescription());
            }
            return reviewRatingRepository.save(review);
        }
        throw new ResourceNotFoundException("Review not found for userId: " + updateReviewDTO.getUserId());
    }

    public ReviewRating  getUserReview(Long recipeId, Long userId) throws ReviewNotFoundException {
        return reviewRatingRepository.findByUserIdAndRecipeId(userId, recipeId).orElseThrow(()->new ReviewNotFoundException("No relation"));
    }

    public List<ReviewRating> getAllReviews(Long recipeId) {
        List<ReviewRating> reviews = reviewRatingRepository.findAllByRecipeId(recipeId);
        return reviews;
    }

    public void deleteReview(Long userId, Long recipeId) {
        reviewRatingRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }
    public Double getAverageRating(Long recipeId)throws ResourceNotFoundException {
        List<ReviewRating> reviews = reviewRatingRepository.findAllByRecipeId(recipeId);
        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found for recipeId: " + recipeId);
        }
        double averageRating = reviews.stream()
                .mapToDouble(ReviewRating::getRating)
                .average()
                .orElse(0.0);

        return averageRating;
    }
}
