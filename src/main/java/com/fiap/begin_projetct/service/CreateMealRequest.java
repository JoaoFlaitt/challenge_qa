package com.fiap.begin_projetct.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateMealRequest {
    
    @NotBlank(message = "Nome da refeição é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;
    
    @NotEmpty(message = "Refeição deve conter pelo menos um alimento")
    @Valid
    private List<FoodItemRequest> items;
    
    @Data
    public static class FoodItemRequest {
        
        @NotBlank(message = "Nome do alimento é obrigatório")
        private String foodName;
        
        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        private Double quantity;
    }

    public com.fiap.begin_projetct.model.Meal toEntity() {
        com.fiap.begin_projetct.model.Meal m = new com.fiap.begin_projetct.model.Meal();
        m.setName(this.name);
        // Note: items mapping is complex and usually handled in service
        return m;
    }
}
