package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller for serving the main web pages
 */
@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final RecipeService recipeService;
    
    /**
     * Display the home page with featured recipes
     * @param model the model to pass data to the view
     * @return the index view template
     */
    @GetMapping("/")
    public String home(Model model) {
        // Get all recipes and limit to 6 featured recipes for the home page
        List<Recipe> allRecipes = recipeService.getAllRecipes();
        List<Recipe> featuredRecipes = allRecipes.stream()
                .limit(6)
                .toList();
        model.addAttribute("featuredRecipes", featuredRecipes);
        return "index";
    }
    
    /**
     * Display the recipes browsing page
     * TODO: Add model attributes for pre-populated data
     * @return the recipes view template
     */
    @GetMapping("/recipes")
    public String recipes() {
        return "recipes";
    }
}
