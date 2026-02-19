package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for managing recipes
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeService recipeService;
    
    /**
     * Get all recipes with optional filtering by difficulty, cuisine, and search term
     * @param difficulty optional difficulty level filter (Easy, Medium, Hard)
     * @param cuisine optional cuisine type filter
     * @param search optional search term to filter by recipe name
     * @return list of recipes matching the filters
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String search) {
        
        List<Recipe> recipes = recipeService.getAllRecipes();
        
        // Apply difficulty filter if provided
        if (difficulty != null && !difficulty.isEmpty()) {
            recipes = recipes.stream()
                    .filter(r -> difficulty.equalsIgnoreCase(r.getDifficultyLevel()))
                    .toList();
        }
        
        // Apply cuisine filter if provided
        if (cuisine != null && !cuisine.isEmpty()) {
            recipes = recipes.stream()
                    .filter(r -> cuisine.equalsIgnoreCase(r.getCuisineType()))
                    .toList();
        }
        
        // Apply search filter if provided
        if (search != null && !search.isEmpty()) {
            final String searchLower = search.toLowerCase();
            recipes = recipes.stream()
                    .filter(r -> r.getName().toLowerCase().contains(searchLower) ||
                                (r.getDescription() != null && r.getDescription().toLowerCase().contains(searchLower)))
                    .toList();
        }
        
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Search recipes by name
     * NOTE: This endpoint is complete and working
     */
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String query) {
        return ResponseEntity.ok(recipeService.searchRecipes(query));
    }
    
    /**
     * Get recipes by difficulty level
     * NOTE: Workshop participants will implement this endpoint using Copilot
     */
    // TODO: Implement GET /api/recipes/difficulty/{level} endpoint
    
    /**
     * Get recipes by cuisine type
     * NOTE: Workshop participants will implement this endpoint using Copilot
     */
    // TODO: Implement GET /api/recipes/cuisine/{type} endpoint
    
    /**
     * Recommend recipes based on available pantry ingredients
     * NOTE: This is an advanced endpoint to be implemented during the workshop
     */
    // TODO: Implement GET /api/recipes/recommendations endpoint
    
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) {
        Recipe saved = recipeService.saveRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable Long id, 
            @Valid @RequestBody Recipe recipe) {
        return recipeService.getRecipeById(id)
                .map(existing -> {
                    recipe.setId(id);
                    return ResponseEntity.ok(recipeService.saveRecipe(recipe));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the Recipe of the Day
     * @return the selected Recipe
     */
    @GetMapping("/recipe-of-the-day")
    public ResponseEntity<Recipe> getRecipeOfTheDay() {
        Recipe recipe = recipeService.getRecipeOfTheDay();
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }
}
