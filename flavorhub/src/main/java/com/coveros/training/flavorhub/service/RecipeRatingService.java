package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.model.RecipeRating;
import com.coveros.training.flavorhub.repository.RecipeRatingRepository;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing recipe ratings and reviews
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RecipeRatingService {
    
    private final RecipeRatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    
    /**
     * Submit a new rating for a recipe
     * @param recipeId the ID of the recipe to rate
     * @param userId the ID of the user submitting the rating
     * @param rating the rating value (1-5)
     * @param review optional review text
     * @return the saved RecipeRating
     * @throws IllegalArgumentException if recipe doesn't exist or user has already rated this recipe
     */
    public RecipeRating submitRating(Long recipeId, Long userId, Integer rating, String review) {
        // Check if recipe exists
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));
        
        // Check if user has already rated this recipe
        Optional<RecipeRating> existingRating = ratingRepository.findByRecipeIdAndUserId(recipeId, userId);
        if (existingRating.isPresent()) {
            throw new IllegalArgumentException("User has already rated this recipe. Use update instead.");
        }
        
        RecipeRating recipeRating = new RecipeRating();
        recipeRating.setRecipe(recipe);
        recipeRating.setUserId(userId);
        recipeRating.setRating(rating);
        recipeRating.setReview(review);
        
        return ratingRepository.save(recipeRating);
    }
    
    /**
     * Update an existing rating
     * @param ratingId the ID of the rating to update
     * @param rating the new rating value (1-5)
     * @param review the new review text
     * @return the updated RecipeRating
     * @throws IllegalArgumentException if rating doesn't exist
     */
    public RecipeRating updateRating(Long ratingId, Integer rating, String review) {
        RecipeRating existingRating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found with id: " + ratingId));
        
        existingRating.setRating(rating);
        existingRating.setReview(review);
        
        return ratingRepository.save(existingRating);
    }
    
    /**
     * Get all ratings for a specific recipe
     * @param recipeId the ID of the recipe
     * @return list of ratings for the recipe
     */
    @Transactional(readOnly = true)
    public List<RecipeRating> getRatingsForRecipe(Long recipeId) {
        return ratingRepository.findByRecipeId(recipeId);
    }
    
    /**
     * Get the average rating for a recipe
     * @param recipeId the ID of the recipe
     * @return the average rating, or null if no ratings exist
     */
    @Transactional(readOnly = true)
    public Double getAverageRating(Long recipeId) {
        return ratingRepository.getAverageRatingForRecipe(recipeId);
    }
    
    /**
     * Get the total count of ratings for a recipe
     * @param recipeId the ID of the recipe
     * @return the number of ratings
     */
    @Transactional(readOnly = true)
    public Long getRatingCount(Long recipeId) {
        return ratingRepository.countByRecipeId(recipeId);
    }
    
    /**
     * Delete a rating
     * @param ratingId the ID of the rating to delete
     */
    public void deleteRating(Long ratingId) {
        ratingRepository.deleteById(ratingId);
    }
    
    /**
     * Get a user's rating for a specific recipe
     * @param recipeId the ID of the recipe
     * @param userId the ID of the user
     * @return the user's rating, if it exists
     */
    @Transactional(readOnly = true)
    public Optional<RecipeRating> getUserRatingForRecipe(Long recipeId, Long userId) {
        return ratingRepository.findByRecipeIdAndUserId(recipeId, userId);
    }
}
