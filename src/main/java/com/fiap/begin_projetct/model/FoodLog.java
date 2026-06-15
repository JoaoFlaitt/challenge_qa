package com.fiap.begin_projetct.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Paciente patient;
    
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
    
    @Column(name = "consumed_at", nullable = false)
    private LocalDateTime consumedAt;
    
    @Column(name = "total_calories", nullable = false)
    private Integer totalCalories;
    
    @Column(name = "total_proteins", nullable = false)
    private Double totalProteins;
    
    @Column(name = "total_carbs", nullable = false)
    private Double totalCarbs;
    
    @Column(name = "total_fats", nullable = false)
    private Double totalFats;
    
    @Column(name = "total_fiber", nullable = false)
    private Double totalFiber;
    
    @Column(name = "total_sodium", nullable = false)
    private Double totalSodium;
    
    @Column(name = "total_sugar", nullable = false)
    private Double totalSugar;
    
    @Column(name = "is_planned", nullable = false)
    private Boolean isPlanned = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calculateTotals();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calculateTotals();
    }
    
    private void calculateTotals() {
        if (food != null && quantity != null) {
            double factor = quantity / 100.0;
            
            totalCalories = (int) (food.getCaloriesPer100g() * factor);
            totalProteins = food.getProteins() * factor;
            totalCarbs = food.getCarbs() * factor;
            totalFats = food.getFats() * factor;
            totalFiber = food.getFiber() * factor;
            totalSodium = food.getSodium() * factor;
            totalSugar = food.getSugar() * factor;
        }
    }
}
