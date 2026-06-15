package com.fiap.begin_projetct.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Paciente patient;
    
    @Column(name = "meal_date", nullable = false)
    private LocalDateTime mealDate;
    
    @JsonManagedReference
    @ToString.Exclude
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealItem> items;
    
    @Column(name = "total_calories")
    private Integer totalCalories;
    
    @Column(name = "total_proteins")
    private Double totalProteins;
    
    @Column(name = "total_carbs")
    private Double totalCarbs;
    
    @Column(name = "total_fats")
    private Double totalFats;
    
    @Column(name = "is_preset", nullable = false)
    private Boolean isPreset = false;
    
    @Column(name = "preset_name")
    private String presetName;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calculateTotals();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }
    
    private void calculateTotals() {
        if (items != null && !items.isEmpty()) {
            totalCalories = items.stream()
                .mapToInt(item -> item.getCalories() != null ? item.getCalories() : 0)
                .sum();
            totalProteins = items.stream()
                .mapToDouble(item -> item.getProteins() != null ? item.getProteins() : 0.0)
                .sum();
            totalCarbs = items.stream()
                .mapToDouble(item -> item.getCarbs() != null ? item.getCarbs() : 0.0)
                .sum();
            totalFats = items.stream()
                .mapToDouble(item -> item.getFats() != null ? item.getFats() : 0.0)
                .sum();
        }
    }
}
