package com.coveros.training.flavorhub.repository;

import com.coveros.training.flavorhub.model.RecipeRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing RecipeRating entities
 */
@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeRating, Long> {
    
    /**
     * Find all ratings for a specific recipe
     */
    List<RecipeRating> findByRecipeId(Long recipeId);
    
    /**
     * Find a user's rating for a specific recipe
     */
    Optional<RecipeRating> findByRecipeIdAndUserId(Long recipeId, Long userId);
    
    /**
     * Count the number of ratings for a recipe
     */
    Long countByRecipeId(Long recipeId);
    
    /**
     * Get the average rating for a recipe
     */
    @Query("SELECT AVG(r.rating) FROM RecipeRating r WHERE r.recipe.id = :recipeId")
    Double getAverageRatingForRecipe(@Param("recipeId") Long recipeId);
}
