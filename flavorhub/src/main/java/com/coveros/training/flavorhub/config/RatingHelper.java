package com.coveros.training.flavorhub.config;

import org.springframework.stereotype.Component;

/**
 * Helper component for rendering star ratings in Thymeleaf templates
 */
@Component("ratingHelper")
public class RatingHelper {
    
    /**
     * Render star rating as HTML string
     * @param rating the average rating (0-5)
     * @return HTML string with filled and empty stars
     */
    public String renderStars(Double rating) {
        if (rating == null || rating == 0.0) {
            return "☆☆☆☆☆";
        }
        
        int fullStars = (int) Math.floor(rating);
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        
        StringBuilder stars = new StringBuilder();
        
        // Add full stars
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        
        // Add half star if applicable (using a filled star for simplicity)
        if (hasHalfStar) {
            stars.append("★");
        }
        
        // Add empty stars
        for (int i = 0; i < emptyStars; i++) {
            stars.append("☆");
        }
        
        return stars.toString();
    }
}
