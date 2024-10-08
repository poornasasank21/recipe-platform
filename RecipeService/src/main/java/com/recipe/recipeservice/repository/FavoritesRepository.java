package com.recipe.recipeservice.repository;

import com.recipe.recipeservice.entity.Favourites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favourites, Long> {
    Optional<Favourites> findByUserId(String userId);
}