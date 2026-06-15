package com.fiap.begin_projetct.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodLogRequest {
    
    @NotNull(message = "ID do paciente é obrigatório")
    private Long pacienteId;
    
    @NotNull(message = "ID da refeição é obrigatório")
    private Long mealId;
    
    @NotNull(message = "ID do alimento é obrigatório")
    private Long foodId;
    
    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.1", message = "Quantidade deve ser maior que zero")
    private Double quantity;
    
    private LocalDateTime consumedAt;
    
    private Boolean isPlanned = false;

    public com.fiap.begin_projetct.model.FoodLog toEntity() {
        com.fiap.begin_projetct.model.FoodLog fl = new com.fiap.begin_projetct.model.FoodLog();
        fl.setConsumedAt(this.consumedAt != null ? this.consumedAt : LocalDateTime.now());
        fl.setQuantity(this.quantity);
        fl.setIsPlanned(this.isPlanned);
        return fl;
    }
}
