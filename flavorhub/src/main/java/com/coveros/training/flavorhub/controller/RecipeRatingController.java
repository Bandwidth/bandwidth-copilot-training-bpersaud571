package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.RecipeRating;
import com.coveros.training.flavorhub.service.RecipeRatingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing recipe ratings and reviews
 */
@RestController
@RequestMapping("/api/recipes/{recipeId}/ratings")
@RequiredArgsConstructor
public class RecipeRatingController {
    
    private final RecipeRatingService ratingService;
    
    /**
     * Submit a new rating for a recipe
     * @param recipeId the ID of the recipe
     * @param request the rating request containing userId, rating, and optional review
     * @return the created RecipeRating
     */
    @PostMapping
    public ResponseEntity<?> submitRating(
            @PathVariable Long recipeId,
            @Valid @RequestBody RatingRequest request) {
        try {
            RecipeRating rating = ratingService.submitRating(
                    recipeId, 
                    request.getUserId(), 
                    request.getRating(), 
                    request.getReview()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(rating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all ratings for a recipe
     * @param recipeId the ID of the recipe
     * @return list of ratings
     */
    @GetMapping
    public ResponseEntity<List<RecipeRating>> getRatings(@PathVariable Long recipeId) {
        return ResponseEntity.ok(ratingService.getRatingsForRecipe(recipeId));
    }
    
    /**
     * Get average rating for a recipe
     * @param recipeId the ID of the recipe
     * @return map containing average rating and count
     */
    @GetMapping("/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable Long recipeId) {
        Double average = ratingService.getAverageRating(recipeId);
        Long count = ratingService.getRatingCount(recipeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", average != null ? average : 0.0);
        response.put("ratingCount", count != null ? count : 0);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update an existing rating
     * @param recipeId the ID of the recipe
     * @param ratingId the ID of the rating to update
     * @param request the updated rating data
     * @return the updated RecipeRating
     */
    @PutMapping("/{ratingId}")
    public ResponseEntity<?> updateRating(
            @PathVariable Long recipeId,
            @PathVariable Long ratingId,
            @Valid @RequestBody RatingRequest request) {
        try {
            RecipeRating updated = ratingService.updateRating(
                    ratingId,
                    request.getRating(),
                    request.getReview()
            );
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete a rating
     * @param recipeId the ID of the recipe
     * @param ratingId the ID of the rating to delete
     * @return no content response
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long recipeId,
            @PathVariable Long ratingId) {
        ratingService.deleteRating(ratingId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get a user's rating for a specific recipe
     * @param recipeId the ID of the recipe
     * @param userId the ID of the user
     * @return the user's rating if it exists
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<RecipeRating> getUserRating(
            @PathVariable Long recipeId,
            @PathVariable Long userId) {
        return ratingService.getUserRatingForRecipe(recipeId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Inner class for rating request data
     */
    public static class RatingRequest {
        @NotNull(message = "User ID is required")
        private Long userId;
        
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;
        
        private String review;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        
        public String getReview() { return review; }
        public void setReview(String review) { this.review = review; }
    }
}
