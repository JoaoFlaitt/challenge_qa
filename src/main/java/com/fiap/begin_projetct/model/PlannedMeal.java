package com.fiap.begin_projetct.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "planned_meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlannedMeal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonBackReference
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_plan_id", nullable = false)
    private DietPlan dietPlan;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;
    
    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;
    
    @Column(name = "meal_type", nullable = false, length = 20)
    private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK
    
    @Column(name = "is_preset_used", nullable = false)
    private Boolean isPresetUsed = false;
    
    @Column(name = "preset_name")
    private String presetName;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
