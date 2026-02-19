package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing recipes
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    private final RecipeRatingService ratingService;
    
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        populateRatingData(recipes);
        return recipes;
    }
    
    public Optional<Recipe> getRecipeById(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        recipe.ifPresent(r -> populateRatingData(r));
        return recipe;
    }
    
    public List<Recipe> getRecipesByDifficulty(String difficultyLevel) {
        List<Recipe> recipes = recipeRepository.findByDifficultyLevel(difficultyLevel);
        populateRatingData(recipes);
        return recipes;
    }
    
    public List<Recipe> getRecipesByCuisine(String cuisineType) {
        List<Recipe> recipes = recipeRepository.findByCuisineType(cuisineType);
        populateRatingData(recipes);
        return recipes;
    }
    
    public List<Recipe> searchRecipes(String searchTerm) {
        List<Recipe> recipes = recipeRepository.findByNameContainingIgnoreCase(searchTerm);
        populateRatingData(recipes);
        return recipes;
    }
    
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
    
    /**
     * Selects a Recipe of the Day using a deterministic algorithm based on the current date.
     * @return the selected Recipe, or null if no recipes exist
     */
    @Transactional(readOnly = true)
    public Recipe getRecipeOfTheDay() {
        List<Recipe> allRecipes = recipeRepository.findAll();
        if (allRecipes.isEmpty()) return null;
        int dayOfYear = java.time.LocalDate.now().getDayOfYear();
        int idx = dayOfYear % allRecipes.size();
        Recipe recipe = allRecipes.get(idx);
        populateRatingData(recipe);
        return recipe;
    }
    
    /**
     * Populate rating data for a single recipe
     */
    private void populateRatingData(Recipe recipe) {
        if (recipe != null) {
            Double avgRating = ratingService.getAverageRating(recipe.getId());
            Long count = ratingService.getRatingCount(recipe.getId());
            recipe.setAverageRating(avgRating);
            recipe.setRatingCount(count != null ? count.intValue() : 0);
        }
    }
    
    /**
     * Populate rating data for multiple recipes
     */
    private void populateRatingData(List<Recipe> recipes) {
        recipes.forEach(this::populateRatingData);
    }
    
    /**
     * Find recipes that can be made based on available ingredients in the pantry
     * NOTE: This method is intentionally left incomplete for workshop participants
     * Participants will use GitHub Copilot to implement this recommendation logic
     */
    // TODO: Implement method to recommend recipes based on pantry ingredients
    
    /**
     * Get recipes that match specific dietary requirements or filters
     * NOTE: This is a more advanced feature to be implemented during the workshop
     */
    // TODO: Implement advanced filtering logic
}
