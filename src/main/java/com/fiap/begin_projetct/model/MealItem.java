package com.fiap.begin_projetct.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meal_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonBackReference
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;
    
    @Column(nullable = false)
    private Double quantity; // in grams
    
    @Column(name = "calories", nullable = false)
    private Integer calories;
    
    @Column(name = "proteins", nullable = false)
    private Double proteins;
    
    @Column(name = "carbs", nullable = false)
    private Double carbs;
    
    @Column(name = "fats", nullable = false)
    private Double fats;
    
    @Column(name = "fiber", nullable = false)
    private Double fiber;
    
    @Column(name = "sodium", nullable = false)
    private Double sodium;
    
    @Column(name = "sugar", nullable = false)
    private Double sugar;
    
    @PrePersist
    @PreUpdate
    protected void calculateNutrients() {
        if (food != null && quantity != null) {
            // Calculate nutrients based on quantity (grams)
            double factor = quantity / 100.0; // convert to per-100g basis
            
            calories = (int) ((food.getCaloriesPer100g() != null ? food.getCaloriesPer100g() : 0) * factor);
            proteins = (food.getProteins() != null ? food.getProteins() : 0.0) * factor;
            carbs = (food.getCarbs() != null ? food.getCarbs() : 0.0) * factor;
            fats = (food.getFats() != null ? food.getFats() : 0.0) * factor;
            fiber = (food.getFiber() != null ? food.getFiber() : 0.0) * factor;
            sodium = (food.getSodium() != null ? food.getSodium() : 0.0) * factor;
            sugar = (food.getSugar() != null ? food.getSugar() : 0.0) * factor;
        }
    }
}
