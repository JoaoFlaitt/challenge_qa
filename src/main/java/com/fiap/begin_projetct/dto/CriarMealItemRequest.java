package com.fiap.begin_projetct.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CriarMealItemRequest {
    
    @NotNull(message = "ID da refeição é obrigatório")
    private Long refeicaoId;
    
    @NotNull(message = "ID do alimento é obrigatório")
    private Long alimentoId;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Double quantidade;
}
