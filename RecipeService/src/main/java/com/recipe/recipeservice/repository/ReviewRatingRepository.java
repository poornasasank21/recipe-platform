package com.recipe.recipeservice.repository;

import com.recipe.recipeservice.entity.ReviewRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRatingRepository extends JpaRepository<ReviewRating, Long> {

    Optional<ReviewRating> findByUserIdAndRecipeId(Long userId, Long recipeId);

    List<ReviewRating> findAllByRecipeId(Long recipeId);

    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}