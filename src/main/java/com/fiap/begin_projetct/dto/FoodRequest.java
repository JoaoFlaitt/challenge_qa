package com.fiap.begin_projetct.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FoodRequest {
    
    @NotBlank(message = "Nome do alimento é obrigatório")
    private String name;
    
    @NotNull(message = "Calorias são obrigatórias")
    @Min(value = 0, message = "Calorias não podem ser negativas")
    private Integer caloriesPer100g;
    
    @NotNull(message = "Proteínas são obrigatórias")
    @DecimalMin(value = "0.0", message = "Proteínas não podem ser negativas")
    private Double proteins;
    
    @NotNull(message = "Carboidratos são obrigatórios")
    @DecimalMin(value = "0.0", message = "Carboidratos não podem ser negativos")
    private Double carbs;
    
    @NotNull(message = "Gorduras são obrigatórias")
    @DecimalMin(value = "0.0", message = "Gorduras não podem ser negativas")
    private Double fats;
    
    @NotNull(message = "Fibras são obrigatórias")
    @DecimalMin(value = "0.0", message = "Fibras não podem ser negativas")
    private Double fiber;
    
    @NotNull(message = "Sódio é obrigatório")
    @DecimalMin(value = "0.0", message = "Sódio não pode ser negativo")
    private Double sodium;
    
    @NotNull(message = "Açúcar é obrigatório")
    @DecimalMin(value = "0.0", message = "Açúcar não pode ser negativo")
    private Double sugar;
    
    @NotNull(message = "Tamanho da porção é obrigatório")
    @DecimalMin(value = "0.1", message = "Tamanho da porção deve ser maior que zero")
    private Double servingSize;
    
    @NotBlank(message = "Unidade da porção é obrigatória")
    private String servingUnit;

    public com.fiap.begin_projetct.model.Food toEntity() {
        com.fiap.begin_projetct.model.Food f = new com.fiap.begin_projetct.model.Food();
        f.setName(this.name);
        f.setCaloriesPer100g(this.caloriesPer100g);
        f.setProteins(this.proteins);
        f.setCarbs(this.carbs);
        f.setFats(this.fats);
        f.setFiber(this.fiber);
        f.setSodium(this.sodium);
        f.setSugar(this.sugar);
        f.setServingSize(this.servingSize);
        f.setServingUnit(this.servingUnit);
        return f;
    }
}
