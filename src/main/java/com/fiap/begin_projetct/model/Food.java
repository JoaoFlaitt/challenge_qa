package com.fiap.begin_projetct.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "foods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false)
    private Integer caloriesPer100g;
    
    @Column(nullable = false)
    private Double proteins;
    
    @Column(nullable = false)
    private Double carbs;
    
    @Column(nullable = false)
    private Double fats;
    
    @Column(nullable = false)
    private Double fiber;
    
    @Column(nullable = false)
    private Double sodium;
    
    @Column(nullable = false)
    private Double sugar;
    
    @Column(name = "serving_size", nullable = false)
    private Double servingSize;
    
    @Column(name = "serving_unit", nullable = false, length = 20)
    private String servingUnit;

    @Column(name = "fdc_id")
    private Long fdcId;

    @Column(nullable = false, length = 20)
    private String source = "MANUAL";
}
